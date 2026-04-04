package grupo10.olympo_academy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ConsultaService;
import grupo10.olympo_academy.services.UserService;

@Controller
public class ConsultasController {
    
    @Autowired 
    private ConsultaService consultaService;

    @Autowired
    private UserService userService;
    
    @PostMapping("/send-consulta")
    public String sendConsulta(@RequestParam String email,
            @RequestParam String name,
            @RequestParam String message,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (message == null || message.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El mensaje no puede estar vacío");
            return "redirect:/app-error";
        }
        
        if(principal != null) {
            //if user is logged in we can save the information
            if(principal.getName().equals(email)){
                User user = userService.findByEmail(email);
                consultaService.sendConsulta(email, name, message, user);
                redirectAttributes.addFlashAttribute("successMessage", "Consulta enviada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No eres quien dices ser");
                return "redirect:/app-error";
            } 
        }else {
            return "redirect:/login"; 
        }
        
        return "redirect:/#exam";
    }
}
