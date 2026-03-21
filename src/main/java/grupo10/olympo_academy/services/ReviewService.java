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

    public List<Review> getReviewsByClasses(Long classesId) {
        return reviewRepository.findByClassesId(classesId);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUserId(user.getId());
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public List<Review> getReviewsByUserAndClasses(User user, Long classesId) {
        return reviewRepository.findByUserIdAndClassesId(user.getId(), classesId);
    }

    public List<Review> getReviewsByUserAndFacility(User user, Long facilityId) {
        return reviewRepository.findByUserIdAndFacilityId(user.getId(), facilityId);
    }

}
