package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.ReviewRepository;

import java.util.List;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByFacility(Long facilityId) {
        return reviewRepository.findByFacilityId(facilityId);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUserId(user.getId());
    }

}
