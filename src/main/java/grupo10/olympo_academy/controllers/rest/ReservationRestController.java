package grupo10.olympo_academy.controllers.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.dto.ReservationDTO;
import grupo10.olympo_academy.dto.ReservationMapper;
import grupo10.olympo_academy.model.Reservation;
import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.services.ReservationService;
import grupo10.olympo_academy.services.UserService;
import io.micrometer.core.ipc.http.HttpSender.Response;

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
    public ResponseEntity<List<ReservationDTO>> getMyReservations(Principal principal) {
        try {
            Optional<User> userOpt = userService.findByEmail(principal.getName());

            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOpt.get();

            List<Reservation> reservations = reservationService.getReservationsByUser(user);

            return ResponseEntity.ok(reservationMapper.toDTOs(reservations));

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getById(@PathVariable Long id) {

        Optional<Reservation> reservationOpt = reservationService.getById(id);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Reservation reservation = reservationOpt.get();
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));

    }

    // CREATE RESERVATION
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO dto, Principal principal) {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Reservation reservation = reservationMapper.toDomain(dto);

        reservationService.confirmReservation(reservation, user);
        dto = reservationMapper.toDTO(reservation);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(location).body(dto);
    }

    // DELETE RESERVATION
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationDTO> deleteReservation(@PathVariable Long id, Principal principal) {

        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Optional <Reservation> reservationOpt= reservationService.getById(id);
        if(reservationOpt.isEmpty()){
           return ResponseEntity.notFound().build();
        }
        Reservation reservation = reservationOpt.get();
        reservationService.cancelReservation(id, user);
        
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }
}