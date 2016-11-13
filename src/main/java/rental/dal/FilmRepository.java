package rental.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.mdl.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {}
