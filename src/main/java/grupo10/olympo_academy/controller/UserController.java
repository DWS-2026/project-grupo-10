package grupo10.olympo_academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
    
    @PostMapping("/login") 
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

    @GetMapping("/userProfile")
    public String getUserProfile() {
        return "userProfile";
    }

    // Mostrar página de registro
    @GetMapping("/register")
    public String showRegister(){
        return "register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                               @RequestParam String email,
                               @RequestParam String password){

        User user = new User();

        user.setName(fullName);
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);

        userService.registerUser(user);

        return "redirect:/login";
    }

}

