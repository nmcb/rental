package rental.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.mdl.User;

public interface Users extends JpaRepository<User, Long> {}
