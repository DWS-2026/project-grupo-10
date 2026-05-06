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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final FacilityService facilityService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserService userService;

    ReviewService(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<List<Review>> getReviewsByFacility(Long facilityId) {
        return reviewRepository.findByFacilityId(facilityId);
    }

    public Optional<List<Review>> getReviewsByClasses(Long classesId) {
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

    public Optional<Review> getById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByUserAndClasses(User user, Long classesId) {
        return reviewRepository.findByUserIdAndClassesId(user.getId(), classesId);
    }

    public List<Review> getReviewsByUserAndFacility(User user, Long facilityId) {
        return reviewRepository.findByUserIdAndFacilityId(user.getId(), facilityId);
    }

    public boolean userReview(Review review, String email) {
        User user = userService.findByEmail(email);
        Long id = user.getId();
        Long idUserReview = review.getUser().getId();
        boolean isAdmin = isAdmin(user);
        if (idUserReview != id && !isAdmin) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles() != null && user.getRoles().contains("ADMIN");
    }
    public Review  buildReviewF(Review review, String email, Long id){
        User user = userService.findByEmail(email);
        review.setUser(user);
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        review.setDate(now.format(formatter));
        Optional <Facility> facilityOpt = facilityService.getFacilityById(id);
        if(facilityOpt.isPresent () ){
            Facility facility = facilityOpt.get();
            review.setFacility(facility);
        }else{
            return null;
        }
        return review;

    }

    public Review buildReviewC(Review review, String email, Long id) {

    User user = userService.findByEmail(email);
    review.setUser(user);

    LocalDate now = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    review.setDate(now.format(formatter));

    Optional<Classes> classesOpt = classesRepository.findById(id);

    if (classesOpt.isPresent()) {
        Classes classes = classesOpt.get();
        review.setClasses(classes);
    } else {
        return null;
    }

    return review;
}
}
