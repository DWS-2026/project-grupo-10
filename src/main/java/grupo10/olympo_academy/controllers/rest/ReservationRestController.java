package grupo10.olympo_academy.controllers.rest;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.dto.ReservationDTO;
import grupo10.olympo_academy.dto.ReservationMapper;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserService userService;

   
    // GET ALL (user logged)
    @GetMapping
    public List<ReservationDTO> getMyReservations(Principal principal) {

        User user = userService.findByEmail(principal.getName());

        List<Reservation> reservations =
                reservationService.getReservationsByUser(user);

        return reservationMapper.toDTOs(reservations);
    }

 
    // GET BY ID
    @GetMapping("/{id}")
    public ReservationDTO getById(@PathVariable Long id) {

        Reservation reservation = reservationService.getById(id);

        return reservationMapper.toDTO(reservation);
    }

    
    // CREATE RESERVATION 
    @PostMapping
    public ReservationDTO createReservation(@RequestBody ReservationDTO dto,
                                            Principal principal) {

        User user = userService.findByEmail(principal.getName());

        Reservation reservation = reservationMapper.toDomain(dto);

        reservationService.confirmReservation(reservation, user);

        return reservationMapper.toDTO(reservation);
    }

   
    // DELETE RESERVATION
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id,
                                  Principal principal) {

        User user = userService.findByEmail(principal.getName());

        reservationService.cancelReservation(id, user);
    }
}