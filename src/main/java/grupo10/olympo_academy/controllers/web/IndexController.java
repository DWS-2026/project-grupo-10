package grupo10.olympo_academy.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;

@Controller
public class IndexController {
    @Autowired
    private ClassesService classesService;
    @Autowired
    private FacilityService facilityService;

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("classes", classesService.getAllClasses());
        return "index";
    }
}
