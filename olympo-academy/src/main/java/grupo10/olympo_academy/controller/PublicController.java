package grupo10.olympo_academy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class PublicController {

    @GetMapping("/bookings")
    public String getbookings() {
        return "bookings";
    }
    @GetMapping("/index")
    public String getIndex () {
        return "index";
    }
    @GetMapping("/reviews")
    public String getreviews() {
        return "reviews";
    }
   
    
}
