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
}

