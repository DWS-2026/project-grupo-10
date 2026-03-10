package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user){

        user.setRole("USER");

        return userRepository.save(user);
    }

    public User findByUsername(String username){

        return userRepository.findByUsername(username);

    }

    public User findByEmail(String email){

        return userRepository.findByEmail(email);

    }

}