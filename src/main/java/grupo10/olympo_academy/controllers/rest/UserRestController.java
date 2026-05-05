package grupo10.olympo_academy.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import grupo10.olympo_academy.dto.UserDTO;
import grupo10.olympo_academy.dto.UserDetailDTO;
import grupo10.olympo_academy.dto.UserMapper;
import grupo10.olympo_academy.dto.UserRegisterDTO;
import grupo10.olympo_academy.dto.PasswordChangeDTO;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ImageService;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
        .map(userMapper::toUserDTO)
        .toList();
        return ResponseEntity.ok(userDTOs);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> me(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            try {
                User user = userService.getUserProfile(principal.getName());
                UserDetailDTO userDTO = userMapper.toDetailDTO(user);
                return ResponseEntity.ok(userDTO);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{id}") 
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @RequestBody UserDTO userDTO,
            HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                //ensure user is using his id
                Long userId = userService.getUserProfile(principal.getName()).getId();
                if(!userId.equals(id)) {
                    return ResponseEntity.status(403).build();
                }
                
                User updatedUser = userService.updateProfile(
                        principal.getName(),
                        userDTO.name(),
                        userDTO.username(),
                        userDTO.phone());

                UserDTO updatedDTO = userMapper.toUserDTO(updatedUser);
                return ResponseEntity.ok(updatedDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeDTO passwordChangeDTO,
            HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                userService.changePassword(principal.getName(), passwordChangeDTO.newPassword());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/image")
    public ResponseEntity<Object> changeProfileImage(@RequestParam MultipartFile imageFile, HttpServletRequest request)
            throws Exception {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String currentUserEmail = principal.getName();
            userService.updateProfileImage(currentUserEmail, imageFile);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/image")
    public ResponseEntity<Object> getProfileImage(HttpServletRequest request) throws Exception {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String currentUserEmail = principal.getName();
            Resource imageFile = imageService.getImageFile(userService.getUserProfile(currentUserEmail).getProfileImage().getId());
            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);
            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //method to cancel your own reservations
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id, HttpServletRequest request) throws Exception {
        
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            try {
                User user = userService.getUserProfile(principal.getName());
                reservationService.cancelReservation(id, user);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping 
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            User newUser = userMapper.toEntity(userRegisterDTO);
            User createdUser = userService.register(newUser);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getId()).toUri();
            return ResponseEntity.created(location).body(userMapper.toUserDTO(createdUser));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

// ADMIN ENDPOINTS

@PutMapping("/admin/{id}")
public ResponseEntity<UserDTO> updateUserFromAdmin(
        @PathVariable Long id,
        @RequestBody UserDTO dto) {

    try {
        User updated = userService.updateUserFromAdmin(
                id,
                dto.name(),
                dto.username(),
                dto.email(),
                dto.phone(),
                null // here without photo, because we have a separate endpoint for that
        );

        return ResponseEntity.ok(userMapper.toUserDTO(updated));

    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@PutMapping("/admin/{id}/image")
public ResponseEntity<Object> changeUserImageFromAdmin(
        @PathVariable Long id,
        @RequestParam MultipartFile imageFile) {

    try {
        userService.updateUserImageFromAdmin(id, imageFile);
        return ResponseEntity.noContent().build();

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PatchMapping("/admin/{id}/block")
public ResponseEntity<Void> blockUser(@PathVariable Long id) {
    userService.blockUser(id);
    return ResponseEntity.ok().build();
}

@PatchMapping("/admin/{id}/unblock")
public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
    userService.unblockUser(id);
    return ResponseEntity.ok().build();
}

@DeleteMapping("/admin/{id}")
public ResponseEntity<Void> deleteUserFromAdmin(@PathVariable Long id) {
    try {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("/admin/{id}")
public ResponseEntity<UserDetailDTO> getUserProfileAsAdmin(@PathVariable Long id) {
    try {
        User user = userService.getById(id);
        if (user == null) return ResponseEntity.notFound().build();

        UserDetailDTO dto = userMapper.toDetailDTO(user);
        return ResponseEntity.ok(dto);

    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}


}
