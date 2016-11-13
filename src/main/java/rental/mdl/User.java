package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private int bonus;
    @OneToMany @OrderBy("checkout asc")
    private List<Rental> rentals;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBonus() {
        return bonus;
    }

    @JsonIgnore
    public List<Rental> getRentals() {
        return rentals;
    }


    // Control Flow Logic

    public int addBonus(int points) {
        return (this.bonus += points);
    }
}
