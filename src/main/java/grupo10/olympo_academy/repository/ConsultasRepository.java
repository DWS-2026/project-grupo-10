package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import grupo10.olympo_academy.model.Consulta;

@Repository
public interface ConsultasRepository extends JpaRepository<Consulta, Long> {
    
}
