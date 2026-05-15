package grupo10.olympo_academy.dto;

import java.util.List;

import jakarta.validation.constraints.Email;


public record UserDetailDTO(
    Long id,
    String name,
    @Email
    String email,
    String phone,
    String username,
    String blocked,
    ImageDTO profileImage,
    List<String> roles,
    List<ReservationDTO> reservations,
    List<ReviewDTO> reviews
) {

}
