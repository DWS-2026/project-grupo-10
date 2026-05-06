package grupo10.olympo_academy.controllers.rest;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import grupo10.olympo_academy.dto.DocumentDTO;
import grupo10.olympo_academy.dto.DocumentMapper;
import grupo10.olympo_academy.model.Document;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.DocumentService;
import grupo10.olympo_academy.services.UserService;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentRestController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentMapper documentMapper;

    // ==============================
    // USER ENDPOINTS 
    // ==============================

    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadMyDocument(
            @RequestParam MultipartFile file,
            Principal principal) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByEmail(principal.getName());
        Document doc = documentService.saveDocumentForUser(file, user);

        return ResponseEntity.ok(documentMapper.toDTO(doc));
    }

    @GetMapping("/me")
    public ResponseEntity<DocumentDTO> getMyDocument(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByEmail(principal.getName());
        Document doc = documentService.findDocumentByUser(user);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(documentMapper.toDTO(doc));
    }

    @GetMapping(value = "/me/view", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> viewMyDocument(Principal principal) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByEmail(principal.getName());
        Document doc = documentService.findDocumentByUser(user);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"application/pdf".equals(doc.getContentType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid file type");
        }

        Resource resource = documentService.loadAsResource(doc);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getOriginalName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // ==============================
    // ADMIN ENDPOINTS
    // ==============================

    @GetMapping("/user/{id}")
    public ResponseEntity<DocumentDTO> getUserDocument(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        Document doc = documentService.findDocumentByUserId(id);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(documentMapper.toDTO(doc));
    }

    @GetMapping(value = "/user/{id}/view", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> viewUserDocument(
            @PathVariable Long id,
            Principal principal) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Document doc = documentService.findDocumentByUserId(id);

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"application/pdf".equals(doc.getContentType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid file type");
        }

        Resource resource = documentService.loadAsResource(doc);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getOriginalName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}