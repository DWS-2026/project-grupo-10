package grupo10.olympo_academy.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.ReservationRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ClassesService classesService;


    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getActiveReservationsByUser(User user) {
        return reservationRepository.findByUserAndStatus(user, "Activa");
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

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
        return reservationRepository.existsByFacilityAndStatusAndDayAndStartTime(
                facility, "Activa", day, startTime);
    }

    public boolean hasActiveReservationsForUserAndClassesAtTime(Classes classes, User user, String startTime, String day) {
        return reservationRepository.existsByClassesAndStatusAndUserAndStartTimeAndDay(
                classes, "Activa", user, startTime, day);
    }

    // =====================================================
    // SAVE 
    // =====================================================

    public Reservation save(Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);

        User user = saved.getUser();
        if (user != null) {
            user.getReservations().add(saved);
        }

        return saved;
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

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(Reservation reservation) {

        User user = reservation.getUser();
        if (user != null) {
            user.getReservations().remove(reservation);
        }

        reservationRepository.delete(reservation);
    }

    // =====================================================
    // BUILD RESERVATION (from DTO or controller)
    // =====================================================

    public Reservation buildReservation(Long facilityId,
                                        Long classId,
                                        String name,
                                        String day,
                                        String startTime,
                                        Boolean material) {

        Reservation reservation = new Reservation();
        reservation.setName(name);
        reservation.setDay(day);
        reservation.setStartTime(startTime);
        reservation.setMaterial(material != null && material);
        reservation.setStatus("Pendiente");

        if (classId != null) {
            Classes classes = classesService.getClassById(classId);
            reservation.setClasses(classes);

            if (reservation.getName() == null || reservation.getName().isBlank()) {
                reservation.setName(classes != null ? classes.getName() : name);
            }
        }

        if (facilityId != null) {
            Facility facility = facilityService.getFacilityById(facilityId);
            reservation.setFacility(facility);

            if (reservation.getName() == null || reservation.getName().isBlank()) {
                reservation.setName(facility != null ? facility.getName() : name);
            }
        }

        return reservation;
    }

    // =====================================================
    // VERIFY AND CONFIRM RESERVATION
    // =====================================================

    public Reservation confirmReservation(Reservation reservation, User user) {

        reservation.setUser(user);
        reservation.setStatus("Activa");

        if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
            Classes classes = classesService.getClassById(reservation.getClasses().getId());
            reservation.setClasses(classes);
            classesService.saveClass(classes);
        }

        return reservationRepository.save(reservation);
    }

    // =====================================================
    // VERIFY AND CONFIRM CART 
    // =====================================================

    public List<Reservation> confirmCart(List<Reservation> cart, User user) {

        for (Reservation r : cart) {

            r.setUser(user);
            r.setStatus("Activa");

            if (r.getFacility() != null && r.getFacility().getId() != null) {
                Facility facility = facilityService.getFacilityById(r.getFacility().getId());
                r.setFacility(facility);
            }

            if (r.getClasses() != null && r.getClasses().getId() != null) {
                Classes classes = classesService.getClassById(r.getClasses().getId());
                r.setClasses(classes);
            }
        }

        return reservationRepository.saveAll(cart);
    }

    
    // =====================================================
    // CANCEL RESERVATION
    // =====================================================

    public void cancelReservation(Long id, User user) {

        Reservation reservation = reservationRepository.findById(id).orElse(null);

        if (reservation == null) {
            throw new RuntimeException("La reserva no existe.");
        };

        if (reservation.getUser() == null || !reservation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva.");
        }

        if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
            Classes classes = classesService.getClassById(reservation.getClasses().getId());
            if (classes != null) {
                classesService.saveClass(classes);
            }
        }

        reservationRepository.delete(reservation);
    }

    // =====================================================
    // CARRITO HELPERS (FOR FRONTEND CART FUNCTIONALITY)
    // =====================================================

    public List<Reservation> initCart(List<Reservation> cart) {
        return cart != null ? cart : new ArrayList<>();
    }

    public void addToCart(List<Reservation> cart, Reservation reservation) {
        cart.add(reservation);
    }

    public void removeFromCart(List<Reservation> cart, int index) {
        cart.remove(index);
    }
}

