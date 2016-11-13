package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rental.dal.Films;
import rental.mdl.Film;

import java.util.List;

@Transactional
@RestController
@RequestMapping("/films")
public class FilmController {
    private final ControllerDelegate<Film> delegate;

    @Autowired
    FilmController(Films films) {
        this.delegate = new ControllerDelegate<>(films);
    }

    // CRUD Mapping

    @RequestMapping(value = ControllerDelegate.BASE, method = RequestMethod.POST)
    public ResponseEntity<Film> add(@RequestBody Film film) {
        return delegate.add(film);
    }

    @RequestMapping(value = ControllerDelegate.WITH_ID)
    public Film byId(long id) {
        return delegate.byId(id);
    }

    @RequestMapping(value = ControllerDelegate.BASE)
    public List<Film> findAll() {
        return delegate.findAll();
    }
}
