package grupo10.olympo_academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.services.ReviewService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Mostrar todas las reseñas
    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        model.addAttribute("review", new Review());
        return "reviews"; 
    }

    // Guardar reseña
    @PostMapping("/save")
    public String saveReview(@RequestParam int rating,
                         @RequestParam String comment) {

    Review review = new Review();
    review.setRating(rating);
    review.setComment(comment);
    review.setDate(java.time.LocalDate.now().toString());

    reviewService.saveReview(review);

    return "redirect:/reviews";
}

    // Eliminar reseña
    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }
}
    
