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
    public String getClassById(@PathVariable Long id, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {

        Classes classes = classesService.getClassById(id);
        if (classes == null) {
            redirectAttributes.addFlashAttribute("error404", "Error 404: Elemento no encontrado");
            return "redirect:/error";
        }

        model.addAttribute("classes", classes);
        model.addAttribute("reviews", reviewService.getReviewsByClasses(id));

        if (principal != null) {
            try {
                User user = userService.findByEmail(principal.getName());
                model.addAttribute("user", user);
                model.addAttribute("myReviews", reviewService.getReviewsByUserAndClasses(user, id));
            } catch (Exception ignored) {
                // ignore if user cannot be resolved
            }
        }

        return "classes";
    }

    /////////////////////////////////////////////////////////////
    // Save or delete review for a specific class
    /////////////////////////////////////////////////////////////
    @PostMapping("/classes/{classesId}/review")
    public String saveReview(@PathVariable Long classesId,
            @RequestParam int rating,
            @RequestParam String comment,
            Principal principal) {

        // Verify user is logged in
        if (principal == null) {
            return "redirect:/login";
        }

        // Resolve user
        User user;
        try {
            user = userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        // Resolve the classes
        Classes classes = classesService.getClassById(classesId);
        if (classes == null) {
            return "redirect:/classes?error=notfound";
        }

        // Create and save the review
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        
        // Set current date
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        review.setDate(now.format(formatter));
        
        // Associate the review with the user and the classes
        review.setUser(user);
        review.setClasses(classes);

        // Save the review
        reviewService.saveReview(review);

        // Redirect back to the class page
        return "redirect:/classes/" + classesId;
    }

    @PostMapping("/classes/{classesId}/review/delete")
    public String deleteReview(@PathVariable Long classesId,
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
        if (review == null || review.getUser() == null || review.getClasses() == null) {
            return "redirect:/classes/" + classesId;
        }

        if (!review.getUser().getId().equals(user.getId())
                || !review.getClasses().getId().equals(classesId)) {
            return "redirect:/classes/" + classesId;
        }

        reviewService.deleteReview(id);
        return "redirect:/classes/" + classesId;
    }

}
