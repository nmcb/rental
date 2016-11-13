# Rental

Thanks for taking the time reviewing this code, thus getting to know me.

As the objective was to asses object oriented analysis, modeling skills, Java coding skills, code structuring and API design, I approached the task from an mostly object oriented point of view.  This took me back a couple of years.  I got hooked on functional programming in 2008, most of my thought has gone into understanding functional analysis, functional modeling, functional code structures and functional API design.  As a result I started to program professionally in Scala and in Haskell to study, I invested about 4 years of time in it this decade.  I'm very interested in functional programming, unfortunately I did not program functionally in Java because I got hooked on Scala years before release 1.8 was out, I thought it would only be fair to tell.

No worries though!

I decided on an object oriented point of view in good spirits for a couple of reasons.  First, even though I like functional programming in Scala, and originally applied for a Scala Programmer role at Casumo for that reason, your, and hopefully soon also my company's talent scout, Andrew, told me during the cultural interview that Scala was on its way out.  He gave me the Java test.

(Andrew also told me that Erlang and Clojure were on their way in.  So although I was sad to hear the news, I was happy hear that functional programming  --complemented with the actor model for that matter!--  are still actively being evaluated.  I like Erlang and Clojure,... in a non-typesafe kind of way :-)

Secondly, I chose an object oriented point of view because I didn't want learn 1.8 Java functionally, its lambdas and streams API, within the timespan of 4 hours.  I ended up spending a multiple of that period, but it was worth it, I did enjoy writing Java again.  I wrote it as prototypically Java as I know it.

So without further ado...

---

## Platform and Architecture

The platform is Java 1.8, a spring boot application with a model driven core, `rental.mdl`, repository structured data access layer, `rental.dal`, controller classes that drive the control flow logic `rental` and an additional `api` package that encodes the structure of the data flow.  Lets start with `rental.mdl`.

### Model

The inventory requires films and related entities to be identifiable, thus the interface `WithId`.

```Java
public interface WithId {
    Long getId();
}
```

We model `Film`, `Box` and `User`, all implementing `WithId` as the core entities of our rental application.  A `Film` has a `name`, is of a certain `type` of rental, and can be rented in one-or-more physical `boxes`.  A `Box` with a certain `film` can be `rentedBy` a `User` after it is taken from the inventory on a certain `checkout` date for an estimated `nrOfDays`.  Upon checking out a `Box` with a certain `film` the `User` receives `bonus` points which are stored with the user's `name` so that at any given moment in time one-or-more `boxes` are `rentedBy` a certain `user`.  A picture tells a hundred words...

![rental-model](https://raw.githubusercontent.com/nmcb/rental/master/img/rental-model.png)

Now I hear you say, that's not real UML, and you are right.  I also hear you say, what is that `RentalType`, or that `Price`, again, all in good time my friend.  For now lets just focus on the fact that a physical object in this world, a `Box` sits pretty nicely inbetween a `User` and his or her `Film`.  Lets see that in the model code:

```java
@Entity
public class Film implements WithId {
    @OneToMany(mappedBy = "film") @OrderBy("checkout asc")
    private List<Box> boxes;
    // ...
}
@Entity
public class Box implements WithId {
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Film film;
    @ManyToOne
    private User rentedBy;
    // ...
}
@Entity
public class User implements WithId {
    @OneToMany @OrderBy("checkout asc")
    private List<Box> boxes;
    // ...
```

The `@Entity`, `@OneToMany`, `@ManyToOne` and `@OrderBy` annotations you see are Java Persistency API (JPA) annotations.  As relations are `optional` by default, a `Film` may not be represented by a `boxes` in the inventory, but a box is always related to a certain `Film`.  Likewise a `Box` does not have to be `rentedBy` a `User` and a `User` may rent zero-or-more `boxes` at a certain moment.  When we query a `Box` we eagerly like to know which `film` it contains, and when we query a `User` or a `Film`, the most interesting thing we want to know is whether the `user` or `film` has `boxes` checked out.  As far as relations go, that is all our little rental service needs together with Spring's excellent repository framework based on the underlying Hibernate OR mapper these couple of POJO's provide us to do something like this...

### Inventory

We model the film inventory with spring repositories.  We would like to be able to let the implementation handle the relation between the three entities, maintain changes to the runtime model, find entities by id, query the repository by id, by example, by relation.  We want our inventory cached, etc.  The whole shebang.  Now that is going to be a lot of code, right?

```java
public interface Films extends JpaRepository<Film, Long> {}
public interface Boxes extends JpaRepository<Box, Long> {}
public interface Users extends JpaRepository<User, Long> {}
```

Done!

### Price Calculation

Now that we know that a `Box` containing a certain `Film` is rented by a certain `User` we can select the properties we need to remember when a `Box` is rented.  We need to retain at the moment of checkout:

- The `checkout` date so we can calculate the late charge.
- The `nrOfDays` the user estimates that she will return the box for the same reason.
- The `user` who rented the film so we can blame, that is charge someone if the film is returned late or not at all.

And we model two distinct moments when these properties change, one moment when a filmbox checked out:

```java
@Transient @JsonIgnore
public Box checkout(User user, int nrOfDays) {
    if(!isInStore()) throw new IllegalStateException("not in store");
    this.checkout = LocalDate.now().format(ISO8601);
    this.rentedBy = user;
    this.nrOfDays = nrOfDays;
    return this;
}
```

The other when it is checked in:

```java
@Transient @JsonIgnore
public Box checkin() {
    if (isInStore()) throw new IllegalStateException("not checked out");
    this.checkout = null;
    this.rentedBy = null;
    this.nrOfDays = 0;
    return this;
}
```

A bit of hacking around and refactoring with the different parts of the pricing calculation did make these parts split out as follows.  We encode the calculation of the `checkoutPrice` and the `checkinPrice` for a certain list of `boxes` be calculated by the `Price` class:

```java
public static Price checkoutPrice(List<Box> boxes) {
    long checkout = 0;
    for (Box box : boxes) {
        checkout += price(box.getFilm().getType(), box.getNrOfDays());
    }
    return new Price(checkout);
}

public static Price checkinPrice(List<Box> boxes) {
    long overdue = 0;
    for (Box box : boxes) {
        long total = price(box.getFilm().getType(), box.getTotalNrOfDays());
        long paid  = price(box.getFilm().getType(), box.getNrOfDays());
        overdue += Math.max(total - paid, 0);
    }
    return new Price(overdue);
}
```

At checkout time we loop the `boxes` and calculate the price per `box` based on that box' `RentalType` and estimated `nrOfDays` before `checkin`.  At checkin time we calculate the `total` price, now employing `box` its `totalNrOfDays` which is the derived number of days between `checkout` time and the moment the `Box` is checked in.

```java
public class Box {
    @Transient @JsonIgnore
    public int getTotalNrOfDays() {
        return Period.between(getCheckoutDate(), LocalDate.now()).getDays();
    }
}
```

From this `total` we subtract the price that the user already `paid` at checkout time.  Additionally, please note that the `price` method on the `Price` class itself is just a utility method that delegates to the class `RentalType`.  Talking about types of rentals, it is about time that we introduce that illustrious `RentalType` class (or rather enumeration) because it holds the static configuration that drives all price calculations.  We define three types of rentals:

```java
public enum RentalType {
    NEW(Price.PREMIUM, 1, 2, "New Release"),
    REG(Price.BASIC, 3, 1, "Regular Rental"),
    OLD(Price.BASIC, 5, 1, "Old Film");

    private long price;
    private int offsetDays;
    private int bonusPoints;
    private String label;
    // ...
}
```

Each rental type is parameterised statically with:

- A `price`, the rental price per day
- The `offsetDays`, the first number of days for the price of one day
- The `bonusPoints`, the amount of bonus points the user gets for that rental
- A `label`, which is unused at the moment, but I like to know the literal meaning of codes :-)

And prices per day are statically encoded as constants from within `Price` which itself provides a small wrapper class around a calculated price.  I didn't take the time to focus modeleling a proper money class, ignored currencies, etc., but that could easily be added and encapsulated by `Price` is wanted:

```java
public class Price {
    private long total;
    
    private Price(long total) {
        this.total = total;
    }
    
    public long getTotal() {
        return total;
    }
    
    public static Price checkoutPrice(List<Box> boxes) {
        // ...
    }
    
    public static Price checkinPrice(List<Box> boxes) {
        // ...
    }
    
    // Constants
    public static final int PREMIUM = 40;  // 40 SEK
    public static final int BASIC   = 30;  // 30 SEK
}
```

The triggering of the price calculation happens just before a response is returned to the user from the controller class responsible for checking film boxes in and out.  Controllers are up next.

### 
