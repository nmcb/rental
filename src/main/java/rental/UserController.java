package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rental.dal.UserRepository;
import rental.mdl.User;

import java.net.URI;
import java.util.List;

@Transactional
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository users;

    @Autowired
    UserController(UserRepository users) {
        this.users = users;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<User> add(@RequestBody User user) {
        User added = this.users.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId()).toUri();
        return ResponseEntity.created(location).body(added);
    }

    @RequestMapping("/{id}")
    public User byId(@PathVariable(value = "id") Long id) {
        return users.findOne(id);
    }

    @RequestMapping
    public List<User> all() {
        return users.findAll();
    }
}
