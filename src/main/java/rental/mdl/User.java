package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class User implements WithId {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private int bonus;
    @OneToMany @OrderBy("checkout asc")
    private List<Box> boxes;

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
    public List<Box> getBoxes() {
        return boxes;
    }


    // Control Flow Logic

    public int addBonus(int points) {
        return (this.bonus += points);
    }
}
