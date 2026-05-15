package grupo10.olympo_academy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO (
    @NotBlank(message = "name is required")
    String name,
    @NotBlank(message = "email is required")
    @Email
    String email,
    String phone,
    @NotBlank(message = "username is required")
    String username,
    @NotBlank(message = "password is required")
    String password) 
    {
}
