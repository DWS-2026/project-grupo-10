package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.model.Facility;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
boolean existsByFacilityAndStatus(Facility facility, String status);
List<Reservation> findByUser(User user);
List<Reservation> findByUserAndStatus(User user, String status);


}
