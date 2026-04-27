package grupo10.olympo_academy.dto;

import java.util.List;

public record FacilityDTO (
     Long id,
     String name,
     String description,
     boolean material,
     String type,
     List<Long> reviewsId,
     Long imageId){  
}
