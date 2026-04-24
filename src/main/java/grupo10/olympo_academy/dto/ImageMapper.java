package grupo10.olympo_academy.dto;

import org.mapstruct.Mapper;

import grupo10.olympo_academy.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}
