package grupo10.olympo_academy.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grupo10.olympo_academy.dto.ClassesDTO;
import grupo10.olympo_academy.dto.ClassesMapper;
import grupo10.olympo_academy.dto.ReviewDTO;
import grupo10.olympo_academy.dto.ReviewMapper;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/classes")
public class ClassesRestController {

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesMapper classesMapper;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private FacilityService facilityService;

    @GetMapping
    public ResponseEntity<Page<ClassesDTO>> getAll(
            @PageableDefault(size = 4, sort = "id") Pageable pageable) {

        try {
            Page<Classes> classesPage = classesService.getClasses(pageable);

            if (pageable.getPageNumber() >= classesPage.getTotalPages()) {
                return ResponseEntity.notFound().build();
            }

            Page<ClassesDTO> dtoPage = classesPage.map(classesMapper::toDTO);
            return ResponseEntity.ok(dtoPage);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassesDTO> getById(@PathVariable Long id) {

        Optional<Classes> classesOpt = classesService.getClassById(id);

        if (classesOpt.isPresent()) {
            return ResponseEntity.ok(classesMapper.toDTO(classesOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ClassesDTO> create(@RequestBody ClassesDTO dto) {

        Classes classes = classesMapper.toDomain(dto);

        if (dto.facilityId() != null) {
            Facility facility = facilityService.getFacilityById(dto.facilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            classes.setFacility(facility);
        }

        Classes saved = classesService.saveClass(classes);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(location).body(classesMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassesDTO> update(@PathVariable Long id, @RequestBody ClassesDTO dto) {

        Classes updated = classesMapper.toDomain(dto);

        if (dto.facilityId() != null) {
            Facility facility = facilityService.getFacilityById(dto.facilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            updated.setFacility(facility);
        }

        Classes saved = classesService.updateClass(id, updated);

        return ResponseEntity.ok(classesMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        Optional<Classes> classesOpt = classesService.getClassById(id);

        if (classesOpt.isPresent()) {
            classesService.deleteClass(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByClassesId(@PathVariable Long id) {

        Optional<List<Review>> reviewsOpt = reviewService.getReviewsByClasses(id);

        if (reviewsOpt.isPresent()) {
            List<ReviewDTO> dto = reviewMapper.toDTOs(reviewsOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long id,
            @RequestBody ReviewDTO dto,
            HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        Review review = reviewMapper.toDomain(dto);
        review = reviewService.buildReviewC(review, principal.getName(), id);

        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        Review saved = reviewService.saveReview(review);
        dto = reviewMapper.toDTO(saved);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(location).body(dto);
    }

    @DeleteMapping("/{id}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        Optional<Review> reviewOpt = reviewService.getById(reviewId);

        if (reviewOpt.isPresent()) {

            boolean isOwner = reviewService.userReview(reviewOpt.get(), principal.getName());

            if (isOwner) {
                reviewService.deleteReview(reviewId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
