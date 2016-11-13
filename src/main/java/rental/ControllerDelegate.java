package rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rental.mdl.WithId;

import java.net.URI;
import java.util.List;

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
