package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Entity
public class Rental {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Basic(optional = false)
    private int nrOfDays;
    @Basic(optional = false)
    private String checkout;
    @Basic
    private String checkin;
    @ManyToOne
    private User user;
    @ManyToOne
    private Film film;

    protected Rental() {}

    public Rental(User user, Film film, int nrOfDays) {
        this.user = user;
        this.film = film;
        this.nrOfDays = nrOfDays;
        // TODO Remove time travel (testing late charge fee)
        this.checkout = LocalDate.now().minusDays(7).format(ISO8601);
        this.checkin = null;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
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

    public String getCheckin() {
        return checkin;
    }


    // Control Flow Logic

    @Transient @JsonIgnore
    public Rental checkin() {
        if (isInStore()) throw new IllegalStateException("not checked out");
        checkin = LocalDate.now().format(ISO8601);
        return this;
    }

    @Transient @JsonIgnore
    public boolean isInStore() {
        return (checkin != null);
    }

    @Transient @JsonIgnore
    public LocalDate getCheckoutDate() {
        return (checkout != null) ? LocalDate.parse(checkout, ISO8601) : null;
    }

    @Transient @JsonIgnore
    public LocalDate getCheckinDate() {
        return (checkin != null) ? LocalDate.parse(checkin, ISO8601) : null;
    }

    @Transient @JsonIgnore
    public int getTotalNrOfDays() {
        return Period.between(getCheckoutDate(), getCheckinDate()).getDays();
    }


    // Constants

    private static final DateTimeFormatter ISO8601 = DateTimeFormatter.ISO_LOCAL_DATE;
}
