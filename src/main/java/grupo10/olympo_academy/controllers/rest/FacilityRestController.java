package grupo10.olympo_academy.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grupo10.olympo_academy.dto.FacilityDTO;
import grupo10.olympo_academy.dto.FacilityMapper;
import grupo10.olympo_academy.dto.ReviewDTO;
import grupo10.olympo_academy.dto.ReviewMapper;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.ImageService;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityRestController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewMapper reviewMapper;

    FacilityRestController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<List<FacilityDTO>> getAll() {
        try {
            List<Facility> facilities = facilityService.getAllFacilities();
            List<FacilityDTO> dtoList = facilityMapper.toDTOs(facilities);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDTO> getById(@PathVariable Long id) {
        try {
            Facility facility = facilityService.getFacilityById(id);
            if (facility == null) {
                return ResponseEntity.notFound().build();
            }
            FacilityDTO dto = facilityMapper.toDTO(facility);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<FacilityDTO> getByName(@PathVariable String name) {
        try {
            Facility facility = facilityService.getFacilityByName(name);
            if (facility == null) {
                return ResponseEntity.notFound().build();
            }
            FacilityDTO dto = facilityMapper.toDTO(facility);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FacilityDTO> create(@RequestBody FacilityDTO dto) {
        try {
            Facility facility = facilityMapper.toDomain(dto);
            if (dto.imageId() != null) {
                facility.setFacilityImage(imageService.getImage(dto.imageId()));
            }
            Facility saved = facilityService.saveFacility(facility);
            System.out.println("IMAGE ID: " + dto.imageId());
            System.out.println("REVIEWS: " + dto.reviewsId());
            return ResponseEntity.ok(facilityMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacilityDTO> update(@PathVariable Long id, @RequestBody FacilityDTO dto) {
        try {
            Facility existing = facilityService.getFacilityById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            existing.setName(dto.name());
            existing.setDescription(dto.description());
            existing.setType(dto.type());

            Facility updated = facilityService.saveFacility(existing);
            return ResponseEntity.ok(facilityMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            Facility facility = facilityService.getFacilityById(id);
            if (facility == null) {
                return ResponseEntity.notFound().build();
            }
            facilityService.deleteFacility(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByFacilityId(@PathVariable Long id) {
        try {
            List<Review> reviews = reviewService.getReviewsByFacility(id);
            List<ReviewDTO> dto = reviewMapper.toDTOs(reviews);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{id}/newReview")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable ReviewDTO dto) {
        try {
            Review review = reviewMapper.toDomain(dto);
            Review reviewSaved = reviewService.saveReview(review);
            return ResponseEntity.ok(reviewMapper.toDTO(reviewSaved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/review/{reviewId}")
    public ResponseEntity<Void> deleteReview (@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
