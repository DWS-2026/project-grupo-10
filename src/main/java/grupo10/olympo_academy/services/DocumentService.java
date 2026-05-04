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

        // Real validation of PDF content (magic bytes)
        byte[] bytes = file.getBytes();
        String header = new String(bytes, 0, Math.min(bytes.length, 4));
        if (!header.equals("%PDF")) {
            throw new IOException("Solo se permiten archivos PDF reales");
        }

        // Blocking basic HTML/JS files (extra defense)
        String lowerName = originalName.toLowerCase();        
        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm") || lowerName.endsWith(".js")) {
            throw new IOException("Tipo de archivo no permitido");
        }

        Path uploadDir = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadDir);

        String storageFileName = STORAGE_FILE_PREFIX + user.getId() + STORAGE_FILE_EXTENSION;
        Path path = uploadDir.resolve(storageFileName);
        Files.write(path, file.getBytes());

        Document document = documentRepository.findByUser(user).orElse(new Document());
        document.setOriginalName(originalName);
        document.setFilePath(path.toAbsolutePath().toString());
        document.setContentType("application/pdf");
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