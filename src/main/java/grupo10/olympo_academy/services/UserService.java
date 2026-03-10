package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username){

        return userRepository.findByUsername(username);

    }

    public Optional<User> findByEmail(String email){

        return userRepository.findByEmail(email);

    }

    public User register(User user) throws Exception {

        // Comprobar si el email ya existe
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        // Asignar rol básico por defecto
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }
        
}