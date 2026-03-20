package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
boolean existsByFacilityAndStatus(Facility facility, String status);
boolean existsByClassesAndStatus(Classes classes, String status);


}
