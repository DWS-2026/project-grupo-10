package grupo10.olympo_academy.dto;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Classes;

@Mapper(componentModel = "spring")
public interface ClassesMapper {

    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "imageId", expression = "java(classes.getClassesImage() !=null ? classes.getClassesImage().getId() : null)")
    @Mapping(target = "reviewsId", expression = "java(classes.getReviews().stream().map(r -> r.getId()).collect(java.util.stream.Collectors.toList()))")

    ClassesDTO toDTO(Classes classes);

    List<ClassesDTO> toDTOs(List<Classes> classesList);

    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "classesImage", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Classes toDomain(ClassesDTO dto);
}
