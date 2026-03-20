package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.repository.ReservationRepository;

@Service
public class ReservationService {
  @Autowired
  private ReservationRepository reservationRepository;

  public boolean hasActiveReservations(Facility facility) {
    return reservationRepository.existsByFacilityAndStatus(facility, "Activa");
  }
  public boolean hasActiveReservationsForClasses(Classes classes) {
    return reservationRepository.existsByClassesAndStatus(classes, "Activa");
  }


}
