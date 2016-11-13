package rental.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.mdl.Film;

public interface Films extends JpaRepository<Film, Long> {}
