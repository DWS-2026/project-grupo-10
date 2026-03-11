package grupo10.olympo_academy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class SessionController {
    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session) {

        // we keep usuario in the login
        if (session.getAttribute("usuario") == null) {
            
            return "redirect:/login";
        }
    
    return "miCuenta"; 
    //realmente este controller no hace nada ahora mismo
}
}
