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

    public User login(String email, String rawPassword) {

        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isEmpty()) {
            return null;
        }

        User user = opt.get();
        // esto cambiará cuando usemos spring-security y el password se guarde hasheado
        if (user.getPassword().equals(rawPassword)) {
            return user;
        }

        return null;

    }

    public User register(User user) throws Exception {

        // check if email is already registered
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        // set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

}