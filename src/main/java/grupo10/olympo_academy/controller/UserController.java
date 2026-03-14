package grupo10.olympo_academy.controller;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.UserService;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /////////////////////////////////////////////////////////////////// LOGIN /////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    /////////////////////////////////////////////////////////////////////// USER PROFILE //////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/userProfile")
    public String getProfile(Model model, Principal principal) {

        // Spring Security provides user email through Principal object, we can use it to fetch the user details from the database
        String email = principal.getName();

        User user =  userService.findByEmail(email);

        model.addAttribute("user", user);

        return "userProfile";
    }


    /////////////////////////////////////////////////////////////////// REGISTER  /////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(User user,
            @RequestParam String password2,
            Model model) {

        if (!user.getPassword().equals(password2)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        try {
            userService.register(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    ////////////////////////////////////////////////////////////////// ADMIN  /////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
    @GetMapping("/admin")
    public String getAdmin() {
        return "admin";
    }
}
