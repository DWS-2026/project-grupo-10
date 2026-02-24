package grupo10.olympo_academy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
    
    
    @PostMapping("/login") // <-- processes the form submission
    public String postLogin(
            @RequestParam String user,
            @RequestParam String password,
            Model model) {

        //validates credentials
        boolean ok = "demo@dominio.com".equals(user) && "1234".equals(password);

        if (ok) {
            return "redirect:/userProfile";
        } else {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
            return "login";
        }
    }
    
    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @GetMapping("/userProfile")
    public String getUserProfile() {
        return "userProfile";
    }
}
