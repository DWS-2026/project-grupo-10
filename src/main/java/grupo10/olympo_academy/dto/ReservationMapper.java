package grupo10.olympo_academy.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo10.olympo_academy.model.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "facility.id", target = "facilityId")
    @Mapping(source = "classes.id", target = "classId")
    ReservationDTO toDTO(Reservation reservation);

    List<ReservationDTO> toDTOs(Collection<Reservation> reservations);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "classes", ignore = true)
    Reservation toDomain(ReservationDTO dto);
}
