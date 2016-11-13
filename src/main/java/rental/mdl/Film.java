package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Film {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private RentalType type;
    @OneToMany(mappedBy = "film") @OrderBy("checkout asc")
    private List<Rental> rentals;


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RentalType getType() {
        return type;
    }

    @JsonIgnore
    public List<Rental> getRentals() {
        return rentals;
    }
}
