package grupo10.olympo_academy.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReservationController {
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;

    // Handles adding a reservation to the cart or confirming it immediately.
    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) Long classId,
            @RequestParam String name,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam int duration,
            @RequestParam(required = false) Boolean material,
            @RequestParam(required = false, defaultValue = "add") String action,
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (facilityId == null && classId == null) {
            redirectAttributes.addFlashAttribute("error", "No se ha seleccionado ninguna actividad.");
            return "redirect:/";
        }

        Reservation reservation = buildReservation(facilityId, classId, name, day, startTime, duration, material);
        if (reservation.getFacility() != null
                && reservationService.hasActiveReservationsForFacilityAtTime(
                        reservation.getFacility(),
                        reservation.getDay(),
                        reservation.getStartTime())) {
            redirectAttributes.addFlashAttribute("warning",
                    "Esa instalación ya está reservada a esa hora.");
            return redirectAfterReservation(facilityId, classId);
        }
        if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
            Classes classes = classesService.getClassById(reservation.getClasses().getId());
            if (classes == null || classes.getAvailableSpots() <= 0) {
                redirectAttributes.addFlashAttribute("warning",
                        "No quedan plazas disponibles para esta clase.");
                return redirectAfterReservation(facilityId, classId);
            }
        }

        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            if (reservation.getFacility() != null
                    && reservationService.hasActiveReservationsForUser(reservation.getFacility(), currentUser)) {
                redirectAttributes.addFlashAttribute("warning", "No se puede reservar la misma instalación hasta que se complete la otra.");
                return redirectAfterReservation(facilityId, classId);
            }
            if (reservation.getClasses() != null
                    && reservationService.hasActiveReservationsForUserAndClassesAtTime(reservation.getClasses(), currentUser, reservation.getStartTime(), reservation.getDay())) {
                redirectAttributes.addFlashAttribute("warning", "Ya tienes una reserva activa para esta clase a esa hora.");
                return redirectAfterReservation(facilityId, classId);
            }
        }

        if ("confirm".equalsIgnoreCase(action)) {
            if (principal == null) {
                return "redirect:/login";
            }
            User user = userService.findByEmail(principal.getName());
            reservation.setUser(user);
            reservation.setStatus("Activa");
            if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
                Classes classes = classesService.getClassById(reservation.getClasses().getId());
                if (classes == null || classes.getAvailableSpots() <= 0) {
                    redirectAttributes.addFlashAttribute("warning",
                            "No quedan plazas disponibles para esta clase.");
                    return redirectAfterReservation(facilityId, classId);
                }
                classes.setAvailableSpots(classes.getAvailableSpots() - 1);
                classesService.saveClass(classes);
                reservation.setClasses(classes);
            }
            reservationService.save(reservation);
            redirectAttributes.addFlashAttribute("success", "Reserva confirmada correctamente.");
            return "redirect:/userProfile";
        }

        @SuppressWarnings("unchecked")
        List<Reservation> cart = (List<Reservation>) session.getAttribute("cartReservations");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cartReservations", cart);
        }

        // Prevent duplicate items in the cart
        for (Reservation existing : cart) {
            if (reservation.getFacility() != null
                    && existing.getFacility() != null
                    && reservation.getFacility().getId().equals(existing.getFacility().getId())) {
                redirectAttributes.addFlashAttribute("warning", "Ya tienes esa instalación en el carrito.");
                return redirectAfterReservation(facilityId, classId);
            }
            if (reservation.getClasses() != null
                    && existing.getClasses() != null
                    && reservation.getClasses().getId().equals(existing.getClasses().getId())
                    && reservation.getStartTime() != null
                    && reservation.getDay() != null
                    && reservation.getStartTime().equals(existing.getStartTime())
                    && reservation.getDay().equals(existing.getDay())) {
                redirectAttributes.addFlashAttribute("warning",
                        "Ya tienes esa clase en el carrito a esa hora.");
                return redirectAfterReservation(facilityId, classId);
            }
        }

        cart.add(reservation);
        redirectAttributes.addFlashAttribute("success", "Reserva añadida al carrito.");
        return redirectAfterReservation(facilityId, classId);
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam int index,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        @SuppressWarnings("unchecked")
        List<Reservation> cart = (List<Reservation>) session.getAttribute("cartReservations");
        if (cart == null || index < 0 || index >= cart.size()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo quitar la reserva del carrito.");
            return referer != null ? "redirect:" + referer : "redirect:/";
        }

        cart.remove(index);
        redirectAttributes.addFlashAttribute("success", "Reserva quitada del carrito.");
        return referer != null ? "redirect:" + referer : "redirect:/";
    }

    // Confirms all cart reservations
    @PostMapping("/cart/confirm")
    public String confirmCart(
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Reservation> cart = (List<Reservation>) session.getAttribute("cartReservations");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay reservas en el carrito.");
            return "redirect:/";
        }

        User user = userService.findByEmail(principal.getName());
        List<Reservation> reservations = new ArrayList<>();

        // Build full reservation objects from cart data
        for (Reservation reservation : cart) {
            reservation.setUser(user);
            reservation.setStatus("Activa");
            if (reservation.getFacility() != null && reservation.getFacility().getId() != null) {
                Facility facility = facilityService.getFacilityById(reservation.getFacility().getId());
                reservation.setFacility(facility);
            }
            if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
                Classes classes = classesService.getClassById(reservation.getClasses().getId());
                reservation.setClasses(classes);
            }
            reservations.add(reservation);
        }

        // Count how many spots are needed per class
        Map<Long, Integer> classCounts = new HashMap<>();
        for (Reservation reservation : reservations) {
            if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
                Long classId = reservation.getClasses().getId();
                classCounts.put(classId, classCounts.getOrDefault(classId, 0) + 1);
            }
        }

        // Validate class availability before committing
        for (Map.Entry<Long, Integer> entry : classCounts.entrySet()) {
            Classes classes = classesService.getClassById(entry.getKey());
            int needed = entry.getValue();
            if (classes == null || classes.getAvailableSpots() < needed) {
                redirectAttributes.addFlashAttribute("warning",
                        "No quedan plazas disponibles para una de las clases seleccionadas.");
                return "redirect:/";
            }
        }

        // Validate conflicts and user constraints
        for (Reservation reservation : reservations) {
            if (reservation.getFacility() != null
                    && reservationService.hasActiveReservationsForFacilityAtTime(
                            reservation.getFacility(),
                            reservation.getDay(),
                            reservation.getStartTime())) {
                redirectAttributes.addFlashAttribute("warning",
                        "Esa instalación ya está reservada a esa hora.");
                return "redirect:/";
            }
            if (reservation.getFacility() != null
                    && reservationService.hasActiveReservationsForUser(reservation.getFacility(), user)) {
                redirectAttributes.addFlashAttribute("warning", "No se puede reservar la misma instalación hasta que se complete la otra.");
                return "redirect:/";
            }
            if (reservation.getClasses() != null
                    && reservationService.hasActiveReservationsForUserAndClassesAtTime(reservation.getClasses(), user, reservation.getStartTime(), reservation.getDay())) {
                redirectAttributes.addFlashAttribute("warning", "Ya tienes una reserva activa para esa clase a esa hora.");
                return "redirect:/";
            }
        }

        // Apply spot consumption for classes
        for (Map.Entry<Long, Integer> entry : classCounts.entrySet()) {
            Classes classes = classesService.getClassById(entry.getKey());
            int needed = entry.getValue();
            classes.setAvailableSpots(classes.getAvailableSpots() - needed);
            classesService.saveClass(classes);
        }

        reservationService.saveAll(reservations);
        session.removeAttribute("cartReservations");
        redirectAttributes.addFlashAttribute("success", "Reservas confirmadas correctamente.");
        return "redirect:/userProfile";
    }

    // Cancels a reservation and frees class spots if needed
    @PostMapping("/reservations/cancel")
    public String cancelReservation(
            @RequestParam Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            redirectAttributes.addFlashAttribute("error", "La reserva no existe.");
            return "redirect:/userProfile";
        }

        User user = userService.findByEmail(principal.getName());
        if (reservation.getUser() == null || !reservation.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta reserva.");
            return "redirect:/userProfile";
        }

        if (reservation.getClasses() != null && reservation.getClasses().getId() != null) {
            Classes classes = classesService.getClassById(reservation.getClasses().getId());
            if (classes != null) {
                classes.setAvailableSpots(classes.getAvailableSpots() + 1);
                classesService.saveClass(classes);
            }
        }
        reservationService.delete(reservation);
        redirectAttributes.addFlashAttribute("success", "Reserva cancelada correctamente.");
        return "redirect:/userProfile";
    }

    // Builds a Reservation object from request parameters
    private Reservation buildReservation(Long facilityId,
                                         Long classId,
                                         String name,
                                         String day,
                                         String startTime,
                                         int duration,
                                         Boolean material) {
        Reservation reservation = new Reservation();
        reservation.setName(name);
        reservation.setDay(day);
        reservation.setStartTime(startTime);
        reservation.setDuration(duration);
        reservation.setMaterial(material != null && material);
        reservation.setStatus("Pendiente");

        if (classId != null) {
            Classes classes = classesService.getClassById(classId);
            reservation.setClasses(classes);
            if (reservation.getName() == null || reservation.getName().isBlank()) {
                reservation.setName(classes != null ? classes.getName() : name);
            }
        } else if (facilityId != null) {
            Facility facility = facilityService.getFacilityById(facilityId);
            reservation.setFacility(facility);
            if (reservation.getName() == null || reservation.getName().isBlank()) {
                reservation.setName(facility != null ? facility.getName() : name);
            }
        }

        return reservation;
    }

    // Chooses the best redirect target based on what was being reserved
    private String redirectAfterReservation(Long facilityId, Long classId) {
        if (classId != null) {
            return "redirect:/classes/" + classId;
        }
        if (facilityId != null) {
            return "redirect:/facilities/" + facilityId;
        }
        return "redirect:/";
    }
}





