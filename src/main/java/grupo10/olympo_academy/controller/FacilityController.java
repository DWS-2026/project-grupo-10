package grupo10.olympo_academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ClassesService;


@Controller
public class FacilityController {

    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ClassesService classesService;

    @GetMapping("/bookings")
    public String getBooking() {
        return "bookings";
    }

    @GetMapping("/" )
    public String getIndex(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("classes", classesService.getAllClasses());
        return "index";
    }

    @GetMapping("/facilities/{id}")
    public String getFacilityById(@PathVariable Long id, Model model) {
        model.addAttribute("facility", facilityService.getFacilityById(id));

        return "facility";
    }
   

}
