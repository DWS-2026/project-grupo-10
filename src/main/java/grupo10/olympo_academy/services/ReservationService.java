package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.ReservationRepository;
import java.util.List;

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

  public boolean hasActiveReservationsForUser(Facility facility, User user) {
    return reservationRepository.existsByFacilityAndStatusAndUser(facility, "Activa", user);
  }

  public boolean hasActiveReservationsForFacilityAtTime(Facility facility, String day, String startTime) {
    return reservationRepository.existsByFacilityAndStatusAndDayAndStartTime(facility, "Activa", day, startTime);
  }

  public boolean hasActiveReservationsForUserAndClasses(Classes classes, User user) {
    return reservationRepository.existsByClassesAndStatusAndUser(classes, "Activa", user);
  }

  public boolean hasActiveReservationsForUserAndClassesAtTime(Classes classes, User user, String startTime,
      String day) {
    return reservationRepository.existsByClassesAndStatusAndUserAndStartTimeAndDay(classes, "Activa", user, startTime,
        day);
  }

  public List<Reservation> saveAll(List<Reservation> reservations) {
    List<Reservation> savedList = reservationRepository.saveAll(reservations);

    for (Reservation r : savedList) {
      User user = r.getUser();
      if (user != null) {
        user.getReservations().add(r);
      }
    }
    return savedList;
  }

  public Reservation save(Reservation reservation) {
    Reservation saved = reservationRepository.save(reservation);

    User user = saved.getUser();
    if (user != null) {
      user.getReservations().add(saved);
    }
    return saved;
  }

  public List<Reservation> getReservationsByUser(User user) {
    return reservationRepository.findByUser(user);
  }

  public List<Reservation> getActiveReservationsByUser(User user) {
    return reservationRepository.findByUserAndStatus(user, "Activa");
  }

  public List<Reservation> getReservationsByUserAndStatus(User user, String status) {
    return reservationRepository.findByUserAndStatus(user, status);
  }

  public Reservation getById(Long id) {
    return reservationRepository.findById(id).orElse(null);
  }

  public void delete(Reservation reservation) {
    User user = reservation.getUser();
    if (user != null) {
      user.getReservations().remove(reservation);
    }
    reservationRepository.delete(reservation);
  }

  public float calculateTotalCost(List<Reservation> reservations) {
    float total = 0;
    for (Reservation reservation : reservations) {
      total += reservation.getPrice();
    }
    return total;
  }

}
