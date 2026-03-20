package grupo10.olympo_academy.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.UserService;

@Controller
public class ClassesController {
    @Autowired
    private ClassesService classesService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
  

    @GetMapping("/classes/{id}")
    public String getClassById(@PathVariable Long id, Model model) {
        model.addAttribute("class", classesService.getClassById(id));
        return "classes";
    }


 /////////////////////////////////////////////////////////////
    // Guardar reseña para una classes específica
    /////////////////////////////////////////////////////////////
    @PostMapping("/classes/{classesId}/review")
    public String saveReview(@PathVariable Long classesId,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             Principal principal) {

        // Verificar si el usuario está logueado
        if (principal == null) {
            return "redirect:/login";
        }

        // Obtener el usuario
        User user;
        try {
            user = userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        // Obtener la classes
        Classes classes = classesService.getClassById(classesId);
        if (classes == null) {
            return "redirect:/classes?error=notfound";
        }

        // Crear y configurar la reseña
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        
        // Formatear la fecha
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        review.setDate(now.format(formatter));
        
        // Asignar usuario y classes
        review.setUser(user);
        review.setClasses(classes);

        // Guardar la reseña
        reviewService.saveReview(review);

        // Redirigir a la página de la classes
        return "redirect:/classes/" + classesId;
    }

}
