package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.Consulta;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.ConsultasRepository;

@Service
public class ConsultaService {
    
    @Autowired
    private ConsultasRepository consultasRepository;
    
    public void sendConsulta(String email, String name, String message, User user) {

        Consulta consulta = new Consulta(email, name, message);
        consulta.setUser(user);
        consultasRepository.save(consulta);

    }
}
