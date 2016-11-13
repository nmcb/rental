package rental.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.mdl.Box;

public interface Boxes extends JpaRepository<Box, Long> {}
