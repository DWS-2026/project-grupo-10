package grupo10.olympo_academy.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityRestController {


    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewMapper reviewMapper;


    @GetMapping
    public ResponseEntity<Page<FacilityDTO>> getAll(@PageableDefault(size = 4, sort = "id") Pageable pageable) {
        try {
            Page<Facility> facilityPage = facilityService.getFacilities(pageable);
            if (pageable.getPageNumber() >= facilityPage.getTotalPages()) {
                return ResponseEntity.notFound().build();
            }
            Page<FacilityDTO> dtoPage = facilityPage.map(facilityMapper::toDTO);
            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDTO> getById(@PathVariable Long id) {

        Optional<Facility> facilityOpt = facilityService.getFacilityById(id);
        if (facilityOpt.isPresent()) {
            return ResponseEntity.ok(facilityMapper.toDTO(facilityOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<FacilityDTO> getByName(@PathVariable String name) {
        Optional<Facility> facilityOpt = facilityService.getFacilityByName(name);
        if (facilityOpt.isPresent()) {
            return ResponseEntity.ok(facilityMapper.toDTO(facilityOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FacilityDTO> create(@RequestBody FacilityDTO dto) {

        Facility facility = facilityMapper.toDomain(dto);
        Facility saved = facilityService.saveFacility(facility);
        dto = facilityMapper.toDTO(saved);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacilityDTO> update(@PathVariable Long id, @RequestBody FacilityDTO dto) {
        Optional<Facility> existing = facilityService.getFacilityById(id);
        if (existing.isPresent()) {
            Facility updated = facilityMapper.toDomain(dto);
            updated = facilityService.updateFacility(id, updated);
            return ResponseEntity.ok(facilityMapper.toDTO(updated));

        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FacilityDTO> delete(@PathVariable Long id) {

        Optional<Facility> facility = facilityService.getFacilityById(id);

        if (facility.isPresent()) {
            facilityService.deleteFacility(id);
            return ResponseEntity.ok(facilityMapper.toDTO(facility.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    ////// FACILYTY REVIEWS////////////

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByFacilityId(@PathVariable Long id) {
        Optional<List<Review>> reviewsOpt = reviewService.getReviewsByFacility(id);
        if (reviewsOpt.isPresent()) {
            List<ReviewDTO> dto = reviewMapper.toDTOs(reviewsOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable Long id, @RequestBody ReviewDTO dto,
            HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        Review review = reviewMapper.toDomain(dto);
        review = reviewService.buildReviewF(review, principal.getName(), id);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        Review reviewSaved = reviewService.saveReview(review);
        dto = reviewMapper.toDTO(reviewSaved);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(location).body(dto);

    }

    @DeleteMapping("/{id}/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable Long reviewId, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        Optional<Review> reviewOpt = reviewService.getById(reviewId);
        if (reviewOpt.isPresent()) {
            boolean bool = reviewService.userReview(reviewOpt.get(), principal.getName());
            if (bool ) {
                Review review= reviewOpt.get();
                reviewService.deleteReview(reviewId);
                return ResponseEntity.ok(reviewMapper.toDTO(review));
            }else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }

    }

}
