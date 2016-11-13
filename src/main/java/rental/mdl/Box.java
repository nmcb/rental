package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Entity
public class Box {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(optional = false)
    private Film film;
    @Basic
    private int nrOfDays;
    @Basic
    private String checkout;
    @ManyToOne
    private User rentedBy;

    Box() {}

    public long getId() {
        return id;
    }

    public User getRentedBy() {
        return rentedBy;
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


    // Control Flow Logic

    @Transient @JsonIgnore
    public Box checkout(User user, int nrOfDays) {
        if(!isInStore()) throw new IllegalStateException("not in store");
        this.rentedBy = user;
        this.nrOfDays = nrOfDays;
        // TODO Remove minus 7 days time travel on checkout, testing late charge fee.
        this.checkout = LocalDate.now().minusDays(7).format(ISO8601);
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