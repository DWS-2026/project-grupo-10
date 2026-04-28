package grupo10.olympo_academy.dto;

import java.util.List;

public record ClassesDTO(
    Long id,
    String name,
    String description,
    String trainer,
    List<String> difficulty,
    List<String> days,
    List<String> startTime,
    Integer duration,
    Long facilityId,
    Long imageId,
    List<Long> reviewsId) {
}
