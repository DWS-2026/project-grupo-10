package grupo10.olympo_academy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findByFacilityId(Long facilityId);
    Optional<List<Review>> findByClassesId(Long classesId);
    List<Review> findByUserId(Long userId);
    List<Review> findByUserIdAndClassesId(Long userId, Long classesId);
    List<Review> findByUserIdAndFacilityId(Long userId, Long facilityId);
}



