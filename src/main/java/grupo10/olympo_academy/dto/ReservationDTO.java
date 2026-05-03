package grupo10.olympo_academy.dto;

public record ReservationDTO(
        Long id,
        String name,
        String day,
        String startTime,
        Integer duration,
        String level,
        String status,
        Boolean material,
        Long userId,
        Long facilityId,
        Long classId
) {
}
