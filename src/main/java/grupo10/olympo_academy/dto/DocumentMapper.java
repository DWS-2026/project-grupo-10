package grupo10.olympo_academy.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "viewUrl", expression = "java(\"/api/v1/document/view\")")
    DocumentDTO toDTO(Document document);
} 