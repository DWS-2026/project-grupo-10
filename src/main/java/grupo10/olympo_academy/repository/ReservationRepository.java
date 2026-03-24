package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
boolean existsByFacilityAndStatus(Facility facility, String status);
boolean existsByClassesAndStatus(Classes classes, String status);
boolean existsByFacilityAndStatusAndUser(Facility facility, String status, User user);
boolean existsByFacilityAndStatusAndDayAndStartTime(Facility facility, String status, String day, String startTime);
boolean existsByClassesAndStatusAndUser(Classes classes, String status, User user);
boolean existsByClassesAndStatusAndUserAndStartTimeAndDay(Classes classes, String status, User user, String startTime, String day);
List<Reservation> findByUser(User user);
List<Reservation> findByUserAndStatus(User user, String status);


}


