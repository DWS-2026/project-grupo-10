package grupo10.olympo_academy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class PublicController {

    @GetMapping("/reservas")
    public String getReservas() {
        return "reservas";
    }
    @GetMapping("/index")
    public String getIndex () {
        return "index";
    }
    @GetMapping("/reseñas")
    public String getReseñas() {
        return "reseñas";
    }
   
    
}
