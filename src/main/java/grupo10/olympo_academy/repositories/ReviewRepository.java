package grupo10.olympo_academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Facility;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
