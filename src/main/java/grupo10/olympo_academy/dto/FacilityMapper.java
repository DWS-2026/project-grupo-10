package grupo10.olympo_academy.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Facility;

@Mapper(componentModel = "spring", imports = Collectors.class)
public interface FacilityMapper {

    @Mapping(target = "reviewsId", expression = "java(facility.getReviews().stream().map(review -> review.getId()).collect(Collectors.toList()))")
    @Mapping(target = "imageId", expression = "java(facility.getFacilityImage() != null ? facility.getFacilityImage().getId() : null)")
    FacilityDTO toDTO(Facility facility);

    List<FacilityDTO> toDTOs(List<Facility> facilityList);

    @Mapping(target = "facilityImage", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Facility toDomain(FacilityDTO dto);
}