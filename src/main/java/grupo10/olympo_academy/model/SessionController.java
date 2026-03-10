package grupo10.olympo_academy.model;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

public class SessionController {
    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session) {

        // we keep usuario in the login
        if (session.getAttribute("usuario") == null) {

            
            return "redirect:/login";
        }

    return "miCuenta"; 
}
}
