package grupo10.olympo_academy.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "downloadUrl", expression = "java(\"/api/v1/document/download\")")
    DocumentDTO toDTO(Document document);
} 