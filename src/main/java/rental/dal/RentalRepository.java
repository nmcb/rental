package rental.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.mdl.Film;
import rental.mdl.Rental;
import rental.mdl.User;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Rental findByFilmAndUser(Film film, User user);
}
