package grupo10.olympo_academy.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            if (reservation.getFacility() != null
                    && reservationService.hasActiveReservationsForUser(reservation.getFacility(), currentUser)) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva activa para esta instalación.");
                return redirectAfterReservation(facilityId, classId);
            }
            if (reservation.getClasses() != null
                    && reservationService.hasActiveReservationsForUserAndClasses(reservation.getClasses(), currentUser)) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva activa para esta clase.");
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

        for (Reservation existing : cart) {
            if (reservation.getFacility() != null
                    && existing.getFacility() != null
                    && reservation.getFacility().getId().equals(existing.getFacility().getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes esa instalación en el carrito.");
                return redirectAfterReservation(facilityId, classId);
            }
            if (reservation.getClasses() != null
                    && existing.getClasses() != null
                    && reservation.getClasses().getId().equals(existing.getClasses().getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes esa clase en el carrito.");
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

        for (Reservation reservation : reservations) {
            if (reservation.getFacility() != null
                    && reservationService.hasActiveReservationsForUser(reservation.getFacility(), user)) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva activa para una instalación del carrito.");
                return "redirect:/";
            }
            if (reservation.getClasses() != null
                    && reservationService.hasActiveReservationsForUserAndClasses(reservation.getClasses(), user)) {
                redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva activa para una clase del carrito.");
                return "redirect:/";
            }
        }

        reservationService.saveAll(reservations);
        session.removeAttribute("cartReservations");
        redirectAttributes.addFlashAttribute("success", "Reservas confirmadas correctamente.");
        return "redirect:/userProfile";
    }

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

        reservation.setStatus("Cancelada");
        reservationService.save(reservation);
        redirectAttributes.addFlashAttribute("success", "Reserva cancelada correctamente.");
        return "redirect:/userProfile";
    }

    @PostMapping("/reservations/delete")
    public String deleteReservation(
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
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta reserva.");
            return "redirect:/userProfile";
        }

        reservationService.delete(reservation);
        redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctamente.");
        return "redirect:/userProfile";
    }

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
