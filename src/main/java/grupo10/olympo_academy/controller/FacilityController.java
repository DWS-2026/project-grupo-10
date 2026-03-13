package grupo10.olympo_academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import grupo10.olympo_academy.services.FacilityService;

@Controller
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/bookings")
    public String getBooking() {
        return "bookings";
    }

    @GetMapping("/facilities/{id}")
    public String getFacilityById(@PathVariable Long id, Model model) {
        model.addAttribute("facility", facilityService.getFacilityById(id));

        return "facility";
    }

}
