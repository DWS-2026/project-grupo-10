package grupo10.olympo_academy.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.User;

@Mapper(componentModel = "spring", uses = { ReservationMapper.class, ReviewMapper.class, ImageMapper.class})
public interface UserMapper {

    UserDetailDTO toDetailDTO(User user);

    List<UserDetailDTO> toDetailDTOs(List<User> users);

    UserDTO toUserDTO(User user);

    List<UserDTO> toUserDTOs(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toDomain(UserDetailDTO dto);
}