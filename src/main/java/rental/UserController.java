package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rental.dal.Users;
import rental.mdl.User;

import java.util.List;

@Transactional
@RestController
@RequestMapping("/users")
public class UserController {
    private ControllerDelegate<User> delegate;

    @Autowired
    UserController(Users users) {
        this.delegate = new ControllerDelegate<>(users);
    }

    // CRUD Mapping

    @RequestMapping(value = ControllerDelegate.BASE, method = RequestMethod.POST)
    public ResponseEntity<User> add(@RequestBody User entity) {
        return delegate.add(entity);
    }

    @RequestMapping(value = ControllerDelegate.WITH_ID)
    public User byId(@PathVariable(name = "id") Long id) {
        return delegate.byId(id);
    }

    @RequestMapping(value = ControllerDelegate.BASE)
    public List<User> findAll() {
        return delegate.findAll();
    }
}
