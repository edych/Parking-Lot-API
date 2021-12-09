package com.edych.parking.controller;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.BadRequestException;
import com.edych.parking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/reservation")
    public ReservationDto create(@RequestBody final ReservationDto dto) {
        if (dto.getId() != null) {
            throw new BadRequestException("a request to create a new Reservation cannot have an id");
        }

        return reservationService.create(dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservation/{id}")
    public void delete(@PathVariable final Long id) {
        reservationService.deleteById(id);
    }

    @GetMapping("/reservations")
    public List<ReservationDto> getAllByCustomerId(@RequestParam final Long customerId) {
        return reservationService.getAllByCustomerId(customerId);
    }
}
