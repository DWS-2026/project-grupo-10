package grupo10.olympo_academy.controller;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpSession;

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

    // Redirect root to index
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    /////////////////////////////////////////////////////////////////// LOGIN //////////////////////////////////////////////////////
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    // call to UserService 
    @PostMapping("/login")
    public String postLogin(@RequestParam String email,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {

        User user = userService.login(email, password);

        if (user == null) {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
            return "login";
        }

        // keep user session
        session.setAttribute("usuarioLogeado", user);

        // check if there's a redirect URL saved in session
        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
        if (redirectUrl != null) {
            session.removeAttribute("redirectAfterLogin"); // clean up session
            return "redirect:" + redirectUrl;
        }

        return "redirect:/userProfile";
    }

    @GetMapping("/userProfile")
    public String getProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("usuarioLogeado");
        
        // check if user is logged in
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "userProfile";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        // Redirect to a mapped page (index) instead of root "/" which has no controller
        return "redirect:/index";
    }

    /////////////////////////////////////////////////////////////////// REGISTER //////////////////////////////////////////////////////
    
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
}
    
   