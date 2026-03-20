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

import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.UserService;

@Controller
public class FacilityController {

    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    @GetMapping("/bookings")
    public String getBooking() {
        return "bookings";
    }

    @GetMapping("/facilities/{id}")
    public String getFacilityById(@PathVariable Long id, Model model) {
        model.addAttribute("facility", facilityService.getFacilityById(id));
        return "facility";
    }
 /////////////////////////////////////////////////////////////
    // Guardar reseña para una facility específica
    /////////////////////////////////////////////////////////////
    @PostMapping("/facilities/{facilityId}/review")
    public String saveReview(@PathVariable Long facilityId,
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

        // Obtener la facility
        Facility facility = facilityService.getFacilityById(facilityId);
        if (facility == null) {
            return "redirect:/facility?error=notfound";
        }

        // Crear y configurar la reseña
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        
        // Formatear la fecha
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        review.setDate(now.format(formatter));
        
        // Asignar usuario y facility
        review.setUser(user);
        review.setFacility(facility);

        // Guardar la reseña
        reviewService.saveReview(review);

        // Redirigir a la página de la facility
        return "redirect:/facilities/" + facilityId;
    }
}
