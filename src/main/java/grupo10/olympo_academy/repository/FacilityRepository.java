package grupo10.olympo_academy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByName(String name);
}

