package grupo10.olympo_academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import grupo10.olympo_academy.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
