package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import grupo10.olympo_academy.model.Task;
import grupo10.olympo_academy.model.User;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByUser(User user);

}
