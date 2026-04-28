package grupo10.olympo_academy.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "facility.id", target = "facilityId")
    @Mapping(source = "classes.id", target = "classId")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "classes", ignore = true)
    Review toDomain(ReviewDTO dto);
}
    
