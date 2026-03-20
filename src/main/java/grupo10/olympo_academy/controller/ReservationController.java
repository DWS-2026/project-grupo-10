package grupo10.olympo_academy.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (facilityId == null && classId == null) {
            redirectAttributes.addFlashAttribute("error", "No se ha seleccionado ninguna actividad.");
            return "redirect:/bookings";
        }

        Reservation cartItem = new Reservation();
        cartItem.setName(name);
        cartItem.setDay(day);
        cartItem.setStartTime(startTime);
        cartItem.setDuration(duration);
        cartItem.setMaterial(material != null && material);
        cartItem.setStatus("Pendiente");

        if (classId != null) {
            Classes classes = classesService.getClassById(classId);
            cartItem.setClasses(classes);
            if (cartItem.getName() == null || cartItem.getName().isBlank()) {
                cartItem.setName(classes != null ? classes.getName() : name);
            }
        } else if (facilityId != null) {
            Facility facility = facilityService.getFacilityById(facilityId);
            cartItem.setFacility(facility);
            if (cartItem.getName() == null || cartItem.getName().isBlank()) {
                cartItem.setName(facility != null ? facility.getName() : name);
            }
        }

        @SuppressWarnings("unchecked")
        List<Reservation> cart = (List<Reservation>) session.getAttribute("cartReservations");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cartReservations", cart);
        }

        cart.add(cartItem);
        redirectAttributes.addFlashAttribute("success", "Reserva añadida al carrito.");
        return "redirect:/bookings";
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
            return "redirect:/bookings";
        }

        User user = userService.findByEmail(principal.getName());
        List<Reservation> reservations = new ArrayList<>();

        for (Reservation reservation : cart) {
            reservation.setUser(user);
            reservation.setStatus("Activa");
            reservations.add(reservation);
        }

        reservationService.saveAll(reservations);
        session.removeAttribute("cartReservations");
        redirectAttributes.addFlashAttribute("success", "Reservas confirmadas correctamente.");
        return "redirect:/userProfile";
    }
}
