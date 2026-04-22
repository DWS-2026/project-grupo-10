package grupo10.olympo_academy.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profileImageUrl", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    UserDetailDTO toDTO(User user);

    List<UserDetailDTO> toDTOs(List<User> users);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toDomain(UserDetailDTO dto);
}