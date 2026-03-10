package grupo10.olympo_academy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import grupo10.olympo_academy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    // Necesario para comprobar si un email ya existe
    Optional<User> findByEmail(String email);
}
