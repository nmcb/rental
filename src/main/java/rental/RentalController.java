package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rental.api.Checkin;
import rental.api.Checkout;
import rental.dal.FilmRepository;
import rental.dal.RentalRepository;
import rental.dal.UserRepository;
import rental.mdl.Film;
import rental.mdl.Price;
import rental.mdl.Rental;
import rental.mdl.User;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    RentalController(
            FilmRepository filmRepository,
            UserRepository userRepository,
            RentalRepository rentalRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Price checkout(@RequestBody Checkout request) {
        User user = userRepository.findOne(request.getUserId());
        List<Rental> rentals = new ArrayList<>();
        for (Checkout.Item item : request.getItems()) {
            Film film = filmRepository.findOne(item.getFilmId());
            user.addBonus(film.getType().getBonusPoints());
            rentals.add(rentalRepository.save(new Rental(user, film, item.getNrOfDays())));
        }
        return Price.checkout(rentals);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Price checkin(@RequestBody Checkin request) {
        List<Rental> rentals = new ArrayList<>();
        User user = userRepository.findOne(request.getUserId());
        for (Long filmId : request.getFilmIds()) {
            Film film = filmRepository.findOne(filmId);
            rentals.add(rentalRepository.findByFilmAndUser(film, user).checkin());
        }
        return Price.checkin(rentals);
    }

    @RequestMapping("/{id}")
    public Rental byId(@PathVariable(value = "id") Long id) {
        return rentalRepository.findOne(id);
    }

    @RequestMapping
    public List<Rental> all() {
        return rentalRepository.findAll();
    }
}
