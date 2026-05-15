package grupo10.olympo_academy.dto;

import jakarta.validation.constraints.Email;

public record UserDTO ( //this will be the data shown to any logged user
    String name,
    @Email
    String email,
    String phone,
    String username) 
    {
}
