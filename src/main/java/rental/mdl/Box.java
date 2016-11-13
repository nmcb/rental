package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Entity
public class Box implements WithId {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Film film;
    @Basic
    private int nrOfDays;
    @Basic
    private String checkout;
    @ManyToOne
    private User rentedBy;

    Box() {}

    // Accessors

    public long getId() {
        return id;
    }

    public Film getFilm() {
        return film;
    }

    public int getNrOfDays() {
        return nrOfDays;
    }

    public String getCheckout() {
        return checkout;
    }

    public User getRentedBy() {
        return rentedBy;
    }

    // Control Flow Logic

    @Transient @JsonIgnore
    public Box checkout(User user, int nrOfDays) {
        if(!isInStore()) throw new IllegalStateException("not in store");
        this.checkout = LocalDate.now().minusDays(7).format(ISO8601);
        this.rentedBy = user;
        this.nrOfDays = nrOfDays;
        return this;
    }

    @Transient @JsonIgnore
    public Box checkin() {
        if (isInStore()) throw new IllegalStateException("not checked out");
        this.checkout = null;
        this.rentedBy = null;
        this.nrOfDays = 0;
        return this;
    }

    // Derived Properties

    @Transient @JsonIgnore
    public boolean isInStore() {
        return (checkout == null);
    }

    @Transient @JsonIgnore
    public LocalDate getCheckoutDate() {
        return (checkout != null) ? LocalDate.parse(checkout, ISO8601) : null;
    }

    @Transient @JsonIgnore
    public int getTotalNrOfDays() {
        return Period.between(getCheckoutDate(), LocalDate.now()).getDays();
    }


    // Constants

    private static final DateTimeFormatter ISO8601 = DateTimeFormatter.ISO_LOCAL_DATE;
}
