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

Now I hear you say, that's not real UML, and you are right.  I also hear you say, what is that `RentalType`, or that `Price`, again, all in good time my friend.  For now lets just focus on the fact that a physical object in this world, a `Box` sits pretty nicely inbetween a `User` and his or her `Film`.  And that we know that a `Box` containing a certain `Film` is rented by a certain `User` when the following three properties of that `Box` are not `null`, or know:

- The `checkout` date the film was rented.
- The `nrOfDays` the user estimates that she will return the box.
- The `user` who rented the film.

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

With this we know enough to model the required relations:

### 
