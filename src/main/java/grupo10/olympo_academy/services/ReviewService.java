package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.ReviewRepository;

import org.springframework.transaction.annotation.Transactional;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.repository.ClassesRepository;
import grupo10.olympo_academy.repository.FacilityRepository;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private FacilityRepository facilityRepository;

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

    @Transactional 
    public void deleteReview(Long id) {
        // Obtain the review before deleting it
        Review review = reviewRepository.findById(id).orElse(null);

        if (review != null) {
            // Delete the review from the list in Classes if it exists
            Classes classes = review.getClasses();
            if (classes != null) {
                classes.deleteReview(id);
                classesRepository.save(classes);
            }

            // Delete the review from the list in Facility if it exists
            Facility facility = review.getFacility();
            if (facility != null) {
                facility.deleteReview(id);
                facilityRepository.save(facility);
            }
        }

        // Finally, delete the review from the database
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
