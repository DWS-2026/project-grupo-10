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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/facilities/{id}")
    public String getFacilityById(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        Facility facility = facilityService.getFacilityById(id);

        if (facility == null) {
            redirectAttributes.addFlashAttribute("error404", "Error 404: Elemento no encontrado");
            return "redirect:/error";
        }

        model.addAttribute("facility", facility);
        model.addAttribute("reviews", reviewService.getReviewsByFacility(id));

        if (principal != null) {
            try {
                User user = userService.findByEmail(principal.getName());
                model.addAttribute("user", user);   
                model.addAttribute("myReviews", reviewService.getReviewsByUserAndFacility(user, id));
            } catch (Exception ignored) {
                // ignore if user cannot be resolved
            }
        }
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

    @PostMapping("/facilities/{facilityId}/review/delete")
    public String deleteReview(@PathVariable Long facilityId,
            @RequestParam Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user;
        try {
            user = userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        Review review = reviewService.getById(id);
        if (review == null || review.getUser() == null || review.getFacility() == null) {
            return "redirect:/facilities/" + facilityId;
        }

        if (!review.getUser().getId().equals(user.getId())
                || !review.getFacility().getId().equals(facilityId)) {
            return "redirect:/facilities/" + facilityId;
        }

        reviewService.deleteReview(id);
        return "redirect:/facilities/" + facilityId;
    }
}
