package grupo10.olympo_academy.controllers.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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

        Optional<Classes> classesOpt = classesService.getClassById(id);

        if (!classesOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Classes classes = classesOpt.get();

        model.addAttribute("classes", classes);
        model.addAttribute("reviews", classes.getReviews());

        if (principal != null) {
            try {
                Optional<User> userOpt = userService.findByEmail(principal.getName());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    model.addAttribute("user", user);
                    model.addAttribute("myReviews", reviewService.getReviewsByUserAndClasses(user, id));
                }
            } catch (Exception ignored) {}

            model.addAttribute("availableTimes", classes.getStartTime());
        }

        return "classes";
    }

    @PostMapping("/classes/{classesId}/review")
    public String saveReview(@PathVariable Long classesId,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt;
        try {
            userOpt = userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        User user = userOpt.get();

        Optional<Classes> classesOpt = classesService.getClassById(classesId);
        if (!classesOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Classes classes = classesOpt.get();

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        review.setDate(now.format(formatter));

        review.setUser(user);
        review.setClasses(classes);

        reviewService.saveReview(review);

        return "redirect:/classes/" + classesId;
    }

    @PostMapping("/classes/{classesId}/review/delete")
    public String deleteReview(@PathVariable Long classesId,
                               @RequestParam Long id,
                               Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt;
        try {
            userOpt = userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        User user = userOpt.get();

        Optional<Review> reviewOpt = reviewService.getById(id);

        if (!reviewOpt.isPresent()) {
            return "redirect:/classes/" + classesId;
        }

        Review review = reviewOpt.get();

        if (review.getUser() == null || review.getClasses() == null) {
            return "redirect:/classes/" + classesId;
        }

        if (!review.getUser().getId().equals(user.getId()) ||
            !review.getClasses().getId().equals(classesId)) {
            return "redirect:/classes/" + classesId;
        }

        reviewService.deleteReview(id);
        return "redirect:/classes/" + classesId;
    }
}