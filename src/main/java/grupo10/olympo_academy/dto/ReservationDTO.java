package grupo10.olympo_academy.dto;

public record ReservationDTO(
        Long id,
        String name,
        String day,
        String startTime,
        int duration,
        String status,
        Boolean material,
        Long userId,
        Long facilityId,
        Long classId
) {
}
