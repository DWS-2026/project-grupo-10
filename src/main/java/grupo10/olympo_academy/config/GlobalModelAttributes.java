package grupo10.olympo_academy.config;

import java.security.Principal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.services.UserService;

@ControllerAdvice
@Component
public class GlobalModelAttributes {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedUser(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
    }

    @ModelAttribute
    public void addAuthenticatedUser(Model model, Principal principal) {
        if (principal != null && !model.containsAttribute("user")) {
            try {
                model.addAttribute("user", userService.findByEmail(principal.getName()));
            } catch (Exception ignored) {
                // ignore if user cannot be resolved
            }
        }
    }

    @ModelAttribute("isAdmin")
    public boolean addIsAdmin(Principal principal) {
        if (principal == null) {
            return false;
        }

        try {
            return userService.findByEmail(principal.getName())
                    .getRoles()
                    .contains("ADMIN");
        } catch (Exception ignored) {
            return false;
        }
    }

    @ModelAttribute("token")
    public String addCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        return csrf == null ? "" : csrf.getToken();
    }

    @ModelAttribute("cartReservations")
    public List<Reservation> addCartReservations(HttpSession session) {
        Object raw = session.getAttribute("cartReservations");
        if (raw instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Reservation> cart = (List<Reservation>) raw;
            return cart;
        }
        return Collections.emptyList();
    }

    @ModelAttribute("cartItems")
    public List<CartItem> addCartItems(HttpSession session) {
        Object raw = session.getAttribute("cartReservations");
        if (raw instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Reservation> cart = (List<Reservation>) raw;
            List<CartItem> items = new ArrayList<>();
            for (int i = 0; i < cart.size(); i++) {
                items.add(new CartItem(i, cart.get(i)));
            }
            return items;
        }
        return Collections.emptyList();
    }

    public static class CartItem {
        private final int index;
        private final Reservation reservation;

        public CartItem(int index, Reservation reservation) {
            this.index = index;
            this.reservation = reservation;
        }

        public int getIndex() {
            return index;
        }

        public Reservation getReservation() {
            return reservation;
        }
    }

    @ModelAttribute("hasCartReservations")
    public boolean addHasCartReservations(HttpSession session) {
        Object raw = session.getAttribute("cartReservations");
        if (raw instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Reservation> cart = (List<Reservation>) raw;
            return !cart.isEmpty();
        }
        return false;
    }
}


