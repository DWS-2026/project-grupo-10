package grupo10.olympo_academy.controllers.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import grupo10.olympo_academy.dto.ImageDTO;
import grupo10.olympo_academy.dto.ImageMapper;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.services.ImageService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/{id}/media") // vulnerabilidad crítica - path traversal
    // GET /classes/{classId}/images/{imageId}
    // GET /facilities/{facilityId}/images/{imageId}
    public ResponseEntity<Object> getImageFile(@PathVariable long id)
            throws SQLException, IOException {
        Resource imageFile = imageService.getImageFile(id);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageDTO> createImage(
            @PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());

        URI location = URI.create("/api/v1/" + id + "/images/" + image.getId());
        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @PutMapping("/{id}/media")
    public ResponseEntity<Object> replaceImage(@PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {
        imageService.replaceImageFile(id, imageFile.getInputStream());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable long id) {
        Image image = imageService.getImage(id);

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
