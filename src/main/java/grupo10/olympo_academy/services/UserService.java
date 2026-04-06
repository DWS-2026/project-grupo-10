package grupo10.olympo_academy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import grupo10.olympo_academy.model.Image;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    // With Spring security, we don´t need to implement the login logic ourselves

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Method to register a new user
    public User register(User user) {

        // Check if email is already registered
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }
        // Unique username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username ya registrado");
        }

        // set default role if not provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("USER"));
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProfile(String currentUserEmail, String name, String username, String phone) throws Exception {
        // we ensure that the user is updating their own profile by using the email from
        // the session (currentUserEmail) to fetch the user from the database.
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User trying dangerous things"));

        // Check if the new username is already taken by another user
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username ya registrado");
        }

        // Check if the new email is already taken by another user
        // if (!user.getEmail().equals(email) &&
        // userRepository.findByEmail(email).isPresent()) {
        // throw new Exception("Email ya registrado");
        // }

        // Check if the new phone number is already taken by another user
        if (!user.getPhone().equals(phone) && userRepository.findByPhone(phone).isPresent()) {
            throw new Exception("Número de teléfono ya registrado");
        }

        user.setName(name);
        user.setUsername(username);
        user.setPhone(phone);

        return userRepository.save(user);
    }

    public User updatePassword(String currentUserEmail, String currentPassword, String newPassword,
            String confirmPassword) throws Exception {
        // we ensure that the user is updating his own password by using the email from
        // the session (currentUserEmail) to fetch the user from the database.
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User trying dangerous things"));

        // check if users input matches his current password stored in the database
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new Exception("Contraseña actual incorrecta");
        }

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Las nuevas contraseñas no coinciden");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    public User updateProfileImage(String currentUserEmail, MultipartFile photoFile) throws Exception {
        // we ensure that the user is updating his own profile image by using the email
        // from the session (currentUserEmail) to fetch the user from the database.
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User trying dangerous things"));

        if (photoFile == null || photoFile.isEmpty()) {
            throw new Exception("Debes seleccionar una imagen válida");
        }

        Image previousImage = user.getProfileImage();
        Image newImage = imageService.createImage(photoFile.getInputStream());
        user.setProfileImage(newImage);

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (Exception e) {
            if (newImage.getId() != null) {
                try {
                    imageService.deleteImage(newImage.getId());
                } catch (Exception ignored) {
                    // Best-effort cleanup if user save fails.
                }
            }
            throw e;
        }

        if (previousImage != null && previousImage.getId() != null
                && !previousImage.getId().equals(newImage.getId())) {
            imageService.deleteImage(previousImage.getId());
        }

        return savedUser;
    }

    public User updateUserFromAdmin(Long id, String name, String username, String email, String phone,
            MultipartFile photoFile) throws Exception {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // We make sure the admin is not trying to change the email, username or phone
        // to one that already exists in another user
        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email ya registrado");
        }

        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username ya registrado");
        }

        if (!user.getPhone().equals(phone) && userRepository.findByPhone(phone).isPresent()) {
            throw new Exception("Número de teléfono ya registrado");
        }

        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);

        Long previousImageId = null;

        // If a new profile image is uploaded, update it. Otherwise, keep the existing
        // one.
        if (photoFile != null && !photoFile.isEmpty()) {
            Image previousImage = user.getProfileImage();
            Image newImage = imageService.createImage(photoFile.getInputStream());
            user.setProfileImage(newImage);

            if (previousImage != null && previousImage.getId() != null
                    && !previousImage.getId().equals(newImage.getId())) {
                previousImageId = previousImage.getId();
            }
        }

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (Exception e) {
            if (photoFile != null && !photoFile.isEmpty() && user.getProfileImage() != null
                    && user.getProfileImage().getId() != null) {
                try {
                    imageService.deleteImage(user.getProfileImage().getId());
                } catch (Exception ignored) {
                    // Best-effort cleanup if user save fails.
                }
            }
            throw e;
        }

        if (previousImageId != null) {
            imageService.deleteImage(previousImageId);
        }

        return savedUser;
    }

    public void deleteUserById(Long id) throws Exception {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // We prevent the deletion of admin users to avoid locking ourselves out of the
        // system
        if (user.getRoles().contains("ADMIN")) {
            throw new Exception("No puedes eliminar un administrador");
        }

        // Delete associated image
        if (user.getProfileImage() != null) {
            Long imageId = user.getProfileImage().getId();
            user.setProfileImage(null);
            userRepository.save(user);
            imageService.deleteImage(imageId);
        }

        userRepository.delete(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setBlocked(false);
        userRepository.save(user);
    }

}
