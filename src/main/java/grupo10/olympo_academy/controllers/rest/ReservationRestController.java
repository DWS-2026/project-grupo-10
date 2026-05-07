package grupo10.olympo_academy.controllers.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import grupo10.olympo_academy.dto.ReservationDTO;
import grupo10.olympo_academy.dto.ReservationMapper;
import grupo10.olympo_academy.model.Reservation;

import grupo10.olympo_academy.services.ReservationService;


@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapper reservationMapper;
    

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
}