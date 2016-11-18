## Rental

Thanks for taking the time reviewing this code, thus getting to know me.

As the objective of this test was to assess object oriented analysis, modeling skills, Java coding skills, code structuring and API design, I approached the task from a mostly object oriented point of view.  This took me back a couple of years.  I got hooked on functional programming in 2008, just around the time the Java eco-system was vibrantly discussing the pro's and con's of different implementations of the lambda calculus in the Java language, ultimately leading to the inclusion of the lambda syntax in Java 1.8, in 2014.  Most of my thought has gone into understanding programming from a functional point of view since that time, functional analysis, functional modeling, functional code structures and functional API design.  As a result I started to program in Scala professionally and experimented with Haskell as a means of study, I invested about 4 years of time programming or studying into functional programming just this decade.  I'm very interested in functional programming, but unfortunately I did not program functionally in Java for this test.  I got hooked on Scala years before release 1.8 was out, and that has been my main vehicle programming functionally know for almost 8 years.  I thought it would only be fair to tell.

No worries though!

I decided on an object oriented point of view in good spirits for a couple of reasons.  First, even though I like functional programming in Scala, and originally applied for a Scala Programmer role at Casumo for that reason, your, and hopefully soon also my company's talent scout, Andrew, told me during the cultural interview that Scala was on its way out.  He gave me the Java test.  Its objectives are mentioned above, and the context is clearly object oriented.  I felt compelled to abide.

(Andrew also told me that Erlang and Clojure were on their way in.  So although I was sad to hear the news, I was happy hear that functional programming  --complemented with the actor model for that matter!--  are still actively being evaluated.  I like Erlang and Clojure,... in a non-typesafe kind of way :-)

Secondly, I chose an object oriented point of view because I didn't want learn Java's functional features in such a short timespan as the test suggested I should take, 4 hours or so.  Java's (actually quite prommesing looking) lambdas and streams API, no, walk that road when needed.  I ended up spending a multiple of that 4 hour period, but it was worth it, I did enjoy writing object oriented Java again.  I wrote the code for the test as prototypically Java as I know it.

So without further ado...

---

## Platform and Architecture

The platform is Java 1.8, a spring boot application with a model driven core, `rental.mdl`; a repository structured data access layer, `rental.dal`; controller classes that drive the `rental` control flow logic; and an additional `api` package that encodes the structure of the data flow.  Lets start with `rental.mdl`.

### Model

The inventory requires films and related entities to be identifiable, thus the interface `WithId`.

```Java
public interface WithId {
    Long getId();
}
```

Than we take the physical stance, and model `Film`, `Box` and `User`, all implementing `WithId` as the core entities of our rental application.  A `Film` has a `name`, is of a certain `type` of rental, and can be rented in one-or-more physical `boxes`.  A `Box` contains a film carrier, nowadays probably a DVD, with a certain `film` can be `rentedBy` a `User` after it is taken from the inventory on a certain `checkout` date for an estimated `nrOfDays`.  Upon checking out a `Box` with a certain `film` (note the singular) the `User` receives `bonus` points which are stored with the user's `name` so that at any given moment in time one-or-more `boxes` can be `rentedBy` a certain `user`.  But a picture paints a thousand words...

![rental-model](https://raw.githubusercontent.com/nmcb/rental/master/img/rental-model.png)

Now I hear you say, that's not real UML, and you are right.  I also hear you say, what is that `RentalType`, or that `Price`, again, all in good time my friend.  For now lets just focus on the fact that a physical object in this world, a `Box` sits pretty nicely inbetween a `User` and his or her `Film`, represented by the two dotted crowfeet lines that connect both a `Film` and a `User` to multiple `boxes`.  Lets see that in the model code:

```java
@Entity
public class Film implements WithId {
    // ...
    @OneToMany(mappedBy = "film") @OrderBy("checkout asc")
    private List<Box> boxes;
    // ...
}
@Entity
public class Box implements WithId {
    // ...
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Film film;
    @ManyToOne
    private User rentedBy;
    // ...
}
@Entity
public class User implements WithId {
    // ...
    @OneToMany @OrderBy("checkout asc")
    private List<Box> boxes;
    // ...
```

The `@Entity`, `@OneToMany`, `@ManyToOne` and `@OrderBy` annotations you see are Java Persistency API (JPA) annotations.  As JPA relations are `optional` by default, a `Film` may not be represented in the inventory, that is, the list of `boxes` containing it may be empty, but a box is always related to a certain `Film`, that is a `Box` its `film` can not be `null`.  Likewise a `Box` does not have to be `rentedBy` a `User` and a `User` may rent zero-or-more `boxes` at a certain moment.  When we query a `Box` we eagerly like to know which `film` it contains because the `User` identifies a certain `Film` via the `Box` containing it.  When we query a `User` regarding a specific `Film`, the most interesting thing we want to know is whether the `User` has `boxes` checked out. Hence, we order both lists of boxes by `checkout` date.  As far as relations go, that is all our little rental service needs modelled together with Spring's excellent repository framework based on the underlying Hibernate Object Relational Mapper (ORM) that provides the functionality to generate a data access layer from a couple of annotated Plain Old Java Objects (POJOs).  It provides us to do something like this...

### Inventory

We model the film inventory with Spring repositories.  We would like to be able to let the implementation handle the relation between the three entities, maintain changes to the runtime model, find entities, query the repository by id, by example, by relation.  We want our inventory cached, etc., the whole shebang.  That is going to be a lot of code, right?

```java
public interface Films extends JpaRepository<Film, Long> {}
public interface Boxes extends JpaRepository<Box,  Long> {}
public interface Users extends JpaRepository<User, Long> {}
```

Done!

### Price Calculation

Now that we know that a `Box` containing a certain `film` is `rentedBy` a certain `User` we can select the properties we need to remember when a `Box` is rented, the moment of `checkout`.  We need to retain at that moment:

- The `checkout` date so we can calculate the late charge.
- The `nrOfDays` in which the user estimates that she will return the box, for the same reason.
- The `user` who rented the film so we can blame, that is charge that person if the film is returned late or not at all.

And we model two distinct moments when these properties change, one moment when a filmbox checked out:

```java
public class Box extends WithId {
    // ...
    @Transient @JsonIgnore
    public Box checkout(User user, int nrOfDays) {
        if(!isInStore()) throw new IllegalStateException("not in store");
        this.checkout = LocalDate.now().format(ISO8601);
        this.rentedBy = user;
        this.nrOfDays = nrOfDays;
        return this;
    }
    // ...
}
```

The other when it is checked in:

```java
public class Box extends WithId {
    // ...
    @Transient @JsonIgnore
    public Box checkin() {
        if (isInStore()) throw new IllegalStateException("not checked out");
        this.checkout = null;
        this.rentedBy = null;
        this.nrOfDays = 0;
        return this;
    }
    // ...
}
```

A bit of hacking around and refactoring with the different parts of the pricing calculation did make these parts split out as follows.  We encode the calculation of the `checkoutPrice` and the `checkinPrice` for a certain list of `boxes` be calculated by the `Price` class:

```java
public class Price {
    // ...
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
    // ...
}
```

At checkout time we loop the `boxes` and calculate the price per `box` based on that box' `RentalType` and estimated `nrOfDays` before `checkin`.  At checkin time we calculate the `total` price, now employing `box` its `totalNrOfDays` which is the derived number of days between `checkout` time and the moment the `Box` is checked in.

```java
public class Box extends WithId {
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

Each rental type is parameterised statically with a `price` as, being the rental price per day; the `offsetDays`, the first number of days for the price per day; the `bonusPoints` which the user gets for that specific type of rental; and a `label` which is unused at this moment,...  but retained because I like to know the literal meaning of enumeration codes.  All together they provide for an easy way to calculate the `chargeFor` a given number of days when one has a given rental type.

```java
public enum RentalType {
    // ...
    public long chargeFor(int days) {
        return getPrice() + getPrice() * Math.max(days - getOffsetDays(), 0);
    }
}
```

Easy right?  Prices per day are statically encoded as constants from within `Price` in reference to the test description that re-uses the terms `PREMIUM` and `BASIC` across the price calculation.  The decision to "hardcode" prices expects a healthy deployment cycle in order to be adaptive to business needs, but hey, it's a test, by now we are able to persist attributes _and_ relations, we can imagine a solution in which we implement the `RentalType` enumeration into an `RentalType` entity containing _more_or_less_ "static" information. `Price` is also used to encode a small wrapper class around thec actual calculated price, represented as a long.  I didn't take the time to focus modeleling a proper money class, ignored currencies, etc., but that could easily be added and encapsulated by `Price` if wanted:

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
        // ... implemented via the [Box]->[Film]->[RentalType] chain to `chargeFor`.
    }
    
    public static Price checkinPrice(List<Box> boxes) {
        // ... implemented via the [Box]->[Film]->[RentalType] chain to `chargeFor`.
    }
    
    // Constants
    public static final int PREMIUM = 40;  // 40 SEK
    public static final int BASIC   = 30;  // 30 SEK
}
```

### Controllers

The triggering of the price calculation happens just before a response is returned to the user from the controller class responsible for checking film boxes in and out.  Controllers are up next.  

#### Control Flow Logic

We want to show the call-sides of the price calculation first, containing the complete control flow logic upon the moment of `checkout` and `checkin`.  It looks like this:

```java
@RestController
@RequestMapping("/boxes")
public class BoxController {
    // ...
    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public Price checkout(@RequestBody Checkout request) {
        User user = users.findOne(request.getUserId());
        List<Box> rentals = new ArrayList<>();
        for (Checkout.Item item : request.getItems()) {
            Box box = boxes.findOne(item.getBoxId()).checkout(user, item.getNrOfDays());
            user.addBonus(box.getFilm().getType().getBonusPoints());
            rentals.add(box);
        }
        return checkoutPrice(rentals);
    }

    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    public Price checkin(@RequestBody Checkin request) {
        List<Box> rentals = boxes.findAll(request.getBoxIds());
        Price price = checkinPrice(rentals);
        rentals.forEach(Box::checkin);
        return price;
    }
    // ...
}
```

Upon `checkout` we find the user requesting a `Checkout`.  We loop the `Checkout.Item`s, that wraps the requested `boxId` and the `nrOfDays` that a `user`wants to rent that specific `Box`.  We find the box and call its `checkout` method, passing it the `user` and `nrOfDays` which effectively updates this information in the inventory.  We `addBonus` points to the `user` now that we know how much points to add following the chain from `box` via `film` to `RentalType`, this also effectively updates the inventory.  Then we add the box to the list of boxes for which we want to have the `checkoutPrice` calculated.

The `checking` logic is even simpler from a controller point of view.  We find all the `rentals` that are returned, calculate the `checkinPrice` for the complete list, and call `checkin` on each box.  Remember that the `checkin` method nullifies the boxes' `checkout`, `user` and `nrOfDays` properties, again updating the inventory, thus effectively checking in the boxes.

#### CRUD Flow Logic

We provide REST access to CRUD functionality in order to setup tests, play with the system, etc.  Each of the three controllers contains such (delegating) logic, lets have a look at `FilmController` as a prototypical example providing REST end-points that `add`, `findAll` or find a film `byId`.

```java
@Transactional
@RestController
@RequestMapping("/films")
public class FilmController {
    private final ControllerDelegate<Film> delegate;

    @Autowired
    FilmController(Films films) {
        this.delegate = new ControllerDelegate<>(films);
    }

    // CRUD Mapping

    @RequestMapping(value = ControllerDelegate.BASE, method = RequestMethod.POST)
    public ResponseEntity<Film> add(@RequestBody Film film) {
        return delegate.add(film);
    }

    @RequestMapping(value = ControllerDelegate.WITH_ID)
    public Film byId(@PathVariable(name = "id") Long id) {
        return delegate.byId(id);
    }

    @RequestMapping(value = ControllerDelegate.BASE)
    public List<Film> findAll() {
        return delegate.findAll();
    }
}
```

The logic itself is implemented in a type parameterised `ControllerDelegate` class in order to factor out logic from all three controller instances.  The implementation of the delegate itself is straight forward though.

```java
public class ControllerDelegate<T extends WithId> {
    private final JpaRepository<T, Long> inventory;

    public ControllerDelegate(JpaRepository<T, Long> inventory) {
        this.inventory = inventory;
    }

    // CRUD Delegation

    public ResponseEntity<T> add(T entity) {
        T added = this.inventory.save(entity);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path(WITH_ID)
                .buildAndExpand(added.getId()).toUri();
        return ResponseEntity.created(location).body(added);
    }

    public T byId(Long id) {
        return inventory.findOne(id);
    }

    public List<T> findAll() {
        return inventory.findAll();
    }

    // Constants

    protected static final String BASE    = "";
    protected static final String WITH_ID = "/{id}";
}
```

## Building, Running and Testing Rental

Easy, provided that you have installed `maven` and `curl`.  Just checkout the project and build with:

```bash
mvn clean install
```

This builds a self running jar with the spring boot rental service which can be run with:

```bash
java -jar target/rentals-0.1.jar
```

A couple of seconds later the service is up and running, we added three test scripts:

```bash
./db-load.sh
./checkout.sh
./checkin.sh
```

The first one loads the database with films, users and (in-store) boxes.  The second one checks a couple of boxes out, the last one checks these boxes back in.  Alternatively you could test and play with a REST client.  I think that's about all, or, at least I think it is time to call it a day.  I'll upload the last bits and go to bed.  Let me know what you think.  Happy Hacking !!!

---

## Disclaimer

No time has been spend on:

- Entity validation
- Automated testing
- Error handling
- Performance testing

And probably much more...

But all requirements can be encoded beautifully, simply and elegantly, provided time and a true love for functional programming :-)
