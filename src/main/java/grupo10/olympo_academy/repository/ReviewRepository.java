package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
