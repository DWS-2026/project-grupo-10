package grupo10.olympo_academy.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;

import grupo10.olympo_academy.model.Document;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.DocumentRepository;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    private static final String UPLOAD_DIR = "uploads/documents";
    private static final String STORAGE_FILE_PREFIX = "dni_user_";
    private static final String STORAGE_FILE_EXTENSION = ".pdf";
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";

    public Document saveDocumentForUser(MultipartFile file, User user) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "dni.pdf";
        } else {
            originalName = Paths.get(originalName).getFileName().toString();
        }

        Path uploadDir = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadDir);

        String storageFileName = STORAGE_FILE_PREFIX + user.getId() + STORAGE_FILE_EXTENSION;
        Path path = uploadDir.resolve(storageFileName);
        Files.write(path, file.getBytes());

        Document document = documentRepository.findByUser(user).orElse(new Document());
        document.setOriginalName(originalName);
        document.setFilePath(path.toAbsolutePath().toString());
        document.setContentType(file.getContentType() != null ? file.getContentType() : DEFAULT_CONTENT_TYPE);
        document.setUser(user);

        return documentRepository.save(document);
    }

    public Document findDocumentByUser(User user) {
        return documentRepository.findByUser(user)
                .map(doc -> {
                    Path path = Paths.get(doc.getFilePath());
                    if (!Files.exists(path)) {
                        documentRepository.delete(doc);
                        return null;
                    }
                    return doc;
                })
                .orElse(null);
    }

    public Document findDocumentByUserId(Long userId) {
        return documentRepository.findByUserId(userId)
                .map(doc -> {
                    Path path = Paths.get(doc.getFilePath());
                    if (!Files.exists(path)) {
                        documentRepository.delete(doc);
                        return null;
                    }
                    return doc;
                })
                .orElse(null);
    }

    public Resource loadAsResource(Document doc) throws IOException {
        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + doc.getFilePath());
        }
        return new UrlResource(path.toUri());
    }
}