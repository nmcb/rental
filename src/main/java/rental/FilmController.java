package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rental.dal.FilmRepository;
import rental.mdl.Film;

import java.net.URI;
import java.util.List;

@Transactional
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmRepository films;

    @Autowired
    FilmController(FilmRepository films) {
        this.films = films;
    }


    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Film> add(@RequestBody Film film) {
        Film saved = this.films.save(film);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @RequestMapping("/{id}")
    public Film byId(@PathVariable(value = "id") Long id) {
        return films.findOne(id);
    }

    @RequestMapping
    public List<Film> all() {
        return films.findAll();
    }

}
