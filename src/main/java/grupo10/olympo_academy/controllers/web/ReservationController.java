package grupo10.olympo_academy.controllers.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    // ADD TO CART
    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) Long classId,
            @RequestParam String name,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam(required = false) Boolean material,
            @RequestParam(required = false, defaultValue = "add") String action,
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (facilityId == null && classId == null) {
            redirectAttributes.addFlashAttribute("error", "No se ha seleccionado ninguna actividad.");
            return "redirect:/";
        }

        Reservation reservation = reservationService.buildReservation(
                facilityId, classId, name, day, startTime, material);
        
        // CASE 1: CONFIRM DIRECTLY
        if ("confirm".equalsIgnoreCase(action)) {
            if (principal == null) {
                return "redirect:/login";
            }
            User user = userService.findByEmail(principal.getName());
            reservationService.confirmReservation(reservation, user);
            redirectAttributes.addFlashAttribute("success", "Reserva confirmada correctamente.");
            return "redirect:/userProfile";
        }

        // CASE 2: ADD TO CART
        @SuppressWarnings("unchecked")
        List<Reservation> cart =
                (List<Reservation>) session.getAttribute("cartReservations");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cartReservations", cart);
        }

        cart.add(reservation);

        redirectAttributes.addFlashAttribute("success", "Reserva añadida al carrito.");
        return "redirect:/";
    }

    // REMOVE FROM CART
    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam int index,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        @SuppressWarnings("unchecked")
        List<Reservation> cart =
                (List<Reservation>) session.getAttribute("cartReservations");

        if (cart == null || index < 0 || index >= cart.size()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo quitar la reserva del carrito.");
            return referer != null ? "redirect:" + referer : "redirect:/";
        }

        cart.remove(index);

        redirectAttributes.addFlashAttribute("success", "Reserva quitada del carrito.");
        return referer != null ? "redirect:" + referer : "redirect:/";
    }

    // CONFIRM CART
    @PostMapping("/cart/confirm")
    public String confirmCart(
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Reservation> cart =
                (List<Reservation>) session.getAttribute("cartReservations");

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay reservas en el carrito.");
            return "redirect:/";
        }

        User user = userService.findByEmail(principal.getName());

        reservationService.confirmCart(cart, user);

        session.removeAttribute("cartReservations");

        redirectAttributes.addFlashAttribute("success", "Reservas confirmadas correctamente.");
        return "redirect:/userProfile";
    }

    // CANCEL RESERVATION
    @PostMapping("/reservations/cancel")
    public String cancelReservation(
            @RequestParam Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        reservationService.cancelReservation(id, user);

        redirectAttributes.addFlashAttribute("success", "Reserva cancelada correctamente.");
        return "redirect:/userProfile";
    }
}