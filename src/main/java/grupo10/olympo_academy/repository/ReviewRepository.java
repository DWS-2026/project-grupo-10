package grupo10.olympo_academy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
     // Buscar reseñas por instalación
    List<Review> findByFacilityId(Long facilityId);

    // Buscar reseñas por usuario
    List<Review> findByUserId(Long userId);
}

