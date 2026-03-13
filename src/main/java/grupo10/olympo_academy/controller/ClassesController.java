package grupo10.olympo_academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import grupo10.olympo_academy.services.ClassesService;

@Controller
public class ClassesController {
    @Autowired
    private ClassesService classesService;
    
    @GetMapping("/classes/{id}")
    public String getClassById(@PathVariable Long id, Model model) {
        model.addAttribute("class", classesService.getClassById(id));

        return "clasess";
    }
}
