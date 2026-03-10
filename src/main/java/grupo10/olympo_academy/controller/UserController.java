package grupo10.olympo_academy.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    
    @PostMapping("/login")
    public String postLogin(
            @RequestParam String user,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        
        boolean ok = "demo@dominio.com".equals(user) && "1234".equals(password);

        if (ok) {
            //we keep user session
            session.setAttribute("usuarioLogeado", user);
            return "redirect:/userProfile";
        } else {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    
    @GetMapping("/userProfile")
    public String getUserProfile(HttpSession session) {

        // if user is not login, move to login page
        if (session.getAttribute("usuarioLogeado") == null) {
            return "redirect:/login";
        }

        return "userProfile"; //protected page
    }

    // logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }
}