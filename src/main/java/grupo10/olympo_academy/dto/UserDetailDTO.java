package grupo10.olympo_academy.dto;

import java.util.List;


public record UserDetailDTO(
    Long id,
    String name,
    String email,
    String phone,
    String username,
    String blocked,
    String profileImageUrl,
    List<String> roles,
    List<ReservationDTO> reservations,
    List<ReviewDTO> reviews
) {

}
