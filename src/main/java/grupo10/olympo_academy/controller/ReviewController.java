package grupo10.olympo_academy.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ReviewService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /////////////////////////////////////////////////////////////
    // Show all reviews and user reviews if logged in
    /////////////////////////////////////////////////////////////
    @GetMapping
    public String showReviews(Model model, HttpSession session) {

    User user = (User) session.getAttribute("usuarioLogeado");

    if (user == null) {
        session.setAttribute("redirectAfterLogin", "/reviews");
        model.addAttribute("notLoggedIn", true); // para mostrar aviso en la vista
    } else {
        model.addAttribute("notLoggedIn", false);

        List<Review> myReviews = reviewService.getReviewsByUser(user);
        model.addAttribute("myReviews", myReviews);
    }

    List<Review> allReviews = reviewService.getAllReviews();
    model.addAttribute("reviews", allReviews);
    model.addAttribute("user", user);

    return "reviews";
}

    /////////////////////////////////////////////////////////////
    // Save review
    /////////////////////////////////////////////////////////////

    @PostMapping("/save")
    public String saveReview(@RequestParam int rating,
                             @RequestParam String comment,
                             HttpSession session) {

        User user = (User) session.getAttribute("usuarioLogeado");

        if (user == null) {
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
                               HttpSession session) {

        User user = (User) session.getAttribute("usuarioLogeado");

        if (user == null) {
            return "redirect:/login";
        }

        reviewService.deleteReview(id);

        return "redirect:/reviews";
    }

}