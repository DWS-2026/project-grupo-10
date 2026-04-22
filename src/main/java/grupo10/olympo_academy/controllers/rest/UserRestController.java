package grupo10.olympo_academy.controllers.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonCreator;

import grupo10.olympo_academy.dto.UserDTO;
import grupo10.olympo_academy.dto.UserDetailDTO;
import grupo10.olympo_academy.dto.UserMapper;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> me(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                User user = userService.getUserProfile(principal.getName());
                UserDetailDTO userDetailDTO = userMapper.toDTO(user);
                return ResponseEntity.ok(userDetailDTO);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetailDTO> profile(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                User user = userService.getUserProfile(principal.getName());
                UserDetailDTO userDetailDTO = userMapper.toDTO(user);

                // Set profile image URL manually
                String profileImageUrl = user.getProfileImage() != null ? "/images/id/" + user.getProfileImage().getId()
                        : null;

                // Create updated DTO with profile image URL
                userDetailDTO = new UserDetailDTO(
                        userDetailDTO.id(),
                        userDetailDTO.name(),
                        userDetailDTO.email(),
                        userDetailDTO.phone(),
                        userDetailDTO.username(),
                        userDetailDTO.blocked(),
                        profileImageUrl,
                        userDetailDTO.roles(),
                        null, // reservations - set to null for now
                        null // reviews - set to null for now
                );

                return ResponseEntity.ok(userDetailDTO);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDetailDTO> updateProfile(@RequestBody UserDetailDTO userDetailDTO,
            HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                User updatedUser = userService.updateProfile(
                        principal.getName(),
                        userDetailDTO.name(),
                        userDetailDTO.username(),
                        userDetailDTO.phone());

                UserDetailDTO updatedDTO = userMapper.toDTO(updatedUser);

                // Set profile image URL manually
                String profileImageUrl = updatedUser.getProfileImage() != null
                        ? "/images/id/" + updatedUser.getProfileImage().getId()
                        : null;

                // Create updated DTO with profile image URL
                updatedDTO = new UserDetailDTO(
                        updatedDTO.id(),
                        updatedDTO.name(),
                        updatedDTO.email(),
                        updatedDTO.phone(),
                        updatedDTO.username(),
                        updatedDTO.blocked(),
                        profileImageUrl,
                        updatedDTO.roles(),
                        null, // reservations - set to null for now
                        null // reviews - set to null for now
                );

                return ResponseEntity.ok(updatedDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody String newPassword, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                userService.changePassword(principal.getName(), newPassword);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/image")
    public ResponseEntity<Void> changeProfileImage(@RequestBody String newImageUrl, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                // Note: This endpoint receives a URL string. For a file upload, consider
                // changing
                // the parameter type to MultipartFile and using the service's
                // changeProfileImage method.
                // For now, this is kept as-is for compatibility.
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    @JsonCreator
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            // create new User domain object from DTO (without id, blocked, roles, profileImage)
            User newUser = new User(
                    userDTO.name(),
                    userDTO.email(),
                    userDTO.phone(),
                    userDTO.password(),
                    userDTO.username());

            // we use the service to register the user
            User registeredUser = userService.register(newUser);

            // return created user
            return ResponseEntity.ok(new UserDTO(
                    registeredUser.getName(),
                    registeredUser.getEmail(),
                    registeredUser.getPhone(),
                    registeredUser.getUsername(),
                    null // password is not returned for security reasons
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ADMIN ENDPOINTS : /api/v1/admin/users/{id} , /api/v1/admin/users/{id}/block ,
    // /api/v1/admin/users/{id}/unblock

}
