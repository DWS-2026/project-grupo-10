package grupo10.olympo_academy.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@Component
public class GlobalModelAttributes {

    @ModelAttribute
    public void addLoggedUser(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
    }
}