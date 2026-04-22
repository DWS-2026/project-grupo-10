package grupo10.olympo_academy.dto;

public record ReviewDTO(
    Long id,
    int rating,
    String comment,
    String date,
    Long userId,
    Long facilityId,
    Long classId
) {
}
