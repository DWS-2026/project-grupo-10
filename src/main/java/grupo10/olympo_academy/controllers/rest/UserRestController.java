package grupo10.olympo_academy.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
import grupo10.olympo_academy.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
        .map(userMapper::toUserDTO)
        .toList();
        return ResponseEntity.ok(userDTOs);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> me(Principal principal) {
        if (principal != null) {
            try {
                User user = userService.getUserProfile(principal.getName());
                UserDetailDTO userDTO = userMapper.toDetailDTO(user);
                return ResponseEntity.ok(userDTO);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("{id}") 
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @RequestBody UserDTO userDTO,
            Principal principal) {

        if (principal != null) {
            try {
                //ensure user is using his id
                Long userId = userService.getUserProfile(principal.getName()).getId();
                if(!userId.equals(id)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeDTO passwordChangeDTO,
            Principal principal) {
        if (principal != null) {
            try {
                userService.changePassword(principal.getName(), passwordChangeDTO.newPassword());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/image")
    public ResponseEntity<Object> changeProfileImage(@RequestParam MultipartFile imageFile, Principal principal)
            throws Exception {

        if (principal != null) {
            try {
                userService.updateProfileImage(principal.getName(), imageFile);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/image")
    public ResponseEntity<Object> getProfileImage(Principal principal) throws Exception {

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
