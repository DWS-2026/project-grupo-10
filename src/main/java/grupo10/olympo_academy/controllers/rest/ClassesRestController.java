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

import grupo10.olympo_academy.dto.ClassesDTO;
import grupo10.olympo_academy.dto.ClassesMapper;
import grupo10.olympo_academy.dto.ReviewDTO;
import grupo10.olympo_academy.dto.ReviewMapper;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.ReviewService;

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


    @GetMapping
    public ResponseEntity<List<ClassesDTO>> getAll() {
        try {
            List<Classes> classesList = classesService.getAllClasses();
            List<ClassesDTO> dtoList = classesMapper.toDTOs(classesList);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassesDTO> getById(@PathVariable Long id) {
        try {
            Classes classes = classesService.getClassById(id);
            ClassesDTO dto = classesMapper.toDTO(classes);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ClassesDTO> create(@RequestBody ClassesDTO dto) {
        try {
            Classes saved = classesService.createClass(dto);
            return ResponseEntity.ok(classesMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassesDTO> update(@PathVariable Long id, @RequestBody ClassesDTO dto) {
        try {
            Classes updated = classesService.updateClass(id, dto);
            return ResponseEntity.ok(classesMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            classesService.deleteClass(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByClassesId(@PathVariable Long id) {
        try {
            List<Review> reviews = reviewService.getReviewsByClasses(id);
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
