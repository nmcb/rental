package rental.mdl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Film implements WithId {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private RentalType type;
    @OneToMany(mappedBy = "film") @OrderBy("checkout asc")
    private List<Box> boxes;

    // Accessors

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
    public List<Box> getBoxes() {
        return boxes;
    }
}
