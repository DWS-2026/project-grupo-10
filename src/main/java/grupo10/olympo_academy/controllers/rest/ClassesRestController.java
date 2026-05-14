package grupo10.olympo_academy.controllers.rest;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import grupo10.olympo_academy.dto.ClassesDTO;
import grupo10.olympo_academy.dto.ClassesMapper;
import grupo10.olympo_academy.dto.ImageDTO;
import grupo10.olympo_academy.dto.ImageMapper;
import grupo10.olympo_academy.dto.ReservationDTO;
import grupo10.olympo_academy.dto.ReservationMapper;
import grupo10.olympo_academy.dto.ReviewDTO;
import grupo10.olympo_academy.dto.ReviewMapper;
import grupo10.olympo_academy.model.Classes;
import grupo10.olympo_academy.model.Facility;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.Review;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ClassesService;
import grupo10.olympo_academy.services.FacilityService;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.ReviewService;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

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

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;
    
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserService userService;

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

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<ClassesDTO> delete(@PathVariable Long id) {

        Optional<Classes> classesOpt = classesService.getClassById(id);

        if (classesOpt.isPresent()) {
            classesService.deleteClass(id);
            return ResponseEntity.ok(classesMapper.toDTO(classesOpt.get()));
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
    public ResponseEntity<ReviewDTO> deleteReview(
            @PathVariable Long reviewId,
            HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        Optional<Review> reviewOpt = reviewService.getById(reviewId);

        if (reviewOpt.isPresent()) {

            boolean isOwner = reviewService.userReview(reviewOpt.get(), principal.getName());

            if (isOwner) {
                Review review= reviewOpt.get();
                reviewService.deleteReview(reviewId);
                return ResponseEntity.ok(reviewMapper.toDTO(review));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    ////////// IMAGES///////////

    @GetMapping("/{id}/images")
    public ResponseEntity<Object> getImageClass(@PathVariable long id) throws SQLException, IOException {
        Optional<Classes> classesOpt = classesService.getClassById(id);
        if (classesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Classes classes = classesOpt.get();
        ClassesDTO dto = classesMapper.toDTO(classes);
        Long idImage = dto.imageId();
        if (idImage == null) {
            return ResponseEntity.notFound().build();
        }
        Resource imageFile = imageService.getImageFile(idImage);

        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ImageDTO> deleteClassImage(@PathVariable long id) throws IOException {

        Optional<Classes> classesOpt = classesService.getClassById(id);
        if (classesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Classes classes = classesOpt.get();
        ClassesDTO dto = classesMapper.toDTO(classes);
        Long idImage = dto.imageId();
        if (idImage == null) {
            return ResponseEntity.notFound().build();
        }
        Image image = imageService.getImage(idImage);

        classesService.removeImageClass(id, image);
        imageService.deleteImage(idImage);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<Object> replaceClassImage(@PathVariable long id,@RequestParam MultipartFile imageFile) 
    throws IOException {
        Optional<Classes> classesOpt = classesService.getClassById(id);
        if (classesOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Classes classes = classesOpt.get();
        ClassesDTO dto = classesMapper.toDTO(classes);
        Long idImage = dto.imageId();
        if (idImage == null) {
            Image image;
            if (!imageFile.isEmpty()) {
                image = imageService.createImage(imageFile.getInputStream());
                classes.setClassesImage(image);
                classesService.saveClass(classes);
            return ResponseEntity.noContent().build();
            }else{
                return ResponseEntity.badRequest().build();
            }
        }

        imageService.replaceImageFile(idImage, imageFile.getInputStream());
        return ResponseEntity.noContent().build();
    }
    //////////////// Reservations////////
    /// 
    @PostMapping("/{id}/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@PathVariable Long id ,@RequestBody ReservationDTO dto, Principal principal) {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Reservation reservation = reservationMapper.toDomain(dto);
        reservation = reservationService.sanitize(reservation);
        reservation= reservationService.confirmClassReservation(id,reservation, user );
        if(reservation == null){
            return ResponseEntity.badRequest().build();
        }
        dto = reservationMapper.toDTO(reservation);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(location).body(dto);
    }
    @DeleteMapping("/{id}/reservations/{idR}")
    public ResponseEntity<ReservationDTO> deleteReservation(@PathVariable Long id, @PathVariable Long idR,
            Principal principal) {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Optional<Reservation> reservationOpt = reservationService.getById(idR);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Reservation reservation = reservationOpt.get();
        Boolean permit = userService.userReservation(reservation, user);
        if (!permit) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        reservationService.cancelReservation(idR, user);

        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }
}
