package grupo10.olympo_academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import grupo10.olympo_academy.model.Classes;


public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByName(String name);
}







