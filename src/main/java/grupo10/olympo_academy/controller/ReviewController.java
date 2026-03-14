package grupo10.olympo_academy.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    /////////////////////////////////////////////////////////////
    // Show all reviews and user reviews if logged in
    /////////////////////////////////////////////////////////////
    @GetMapping
    public String showReviews(Model model, Principal principal) {

        User user = null;
        if (principal != null) {
            try {
                user = userService.findByEmail(principal.getName());
            } catch (Exception ignored) {
                // ignore missing user
            }
        }

        if (user == null) {
            model.addAttribute("notLoggedIn", true); // para mostrar aviso en la vista
        } else {
            model.addAttribute("notLoggedIn", false);

            List<Review> myReviews = reviewService.getReviewsByUser(user);
            model.addAttribute("myReviews", myReviews);

            // Only add user to model if we resolved it (don’t override global attribute with null)
            model.addAttribute("user", user);
        }

        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);

        return "reviews";
    }

    /////////////////////////////////////////////////////////////
    // Save review
    /////////////////////////////////////////////////////////////

    @PostMapping("/save")
    public String saveReview(@RequestParam int rating,
                             @RequestParam String comment,
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

        Review review = new Review();

        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now().toString());
        review.setUser(user);

        reviewService.saveReview(review);

        return "redirect:/reviews";
    }


    /////////////////////////////////////////////////////////////
    // Delete review
    /////////////////////////////////////////////////////////////

    @PostMapping("/delete")
    public String deleteReview(@RequestParam Long id,
                               Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            userService.findByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:/login";
        }

        reviewService.deleteReview(id);

        return "redirect:/reviews";
    }

}