package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);
}
