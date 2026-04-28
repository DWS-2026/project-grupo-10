package grupo10.olympo_academy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import grupo10.olympo_academy.model.Document;
import grupo10.olympo_academy.model.User;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByUser(User user);
    Optional<Document> findByUserId(Long userId);
}
