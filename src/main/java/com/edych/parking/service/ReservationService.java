package com.edych.parking.service;

import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.CustomerRepository;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.service.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public ReservationDto create(ReservationDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("customer", dto.getCustomerId()));

        ParkingSpot parkingSpot = parkingSpotRepository.findById(dto.getParkingSpotId())
                .orElseThrow(() -> new NotFoundException("parkingSpot", dto.getParkingSpotId()));

        boolean spotTaken = reservationRepository.existsByParkingSpotId(dto.getParkingSpotId());

        if (spotTaken) {
            String msg = String.format("Parking spot id [%s] is already taken", dto.getParkingSpotId());
            throw new ConflictException(msg);
        }

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .parkingSpot(parkingSpot)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        return ReservationDto.builder()
                .id(saved.getId())
                .customerId(saved.getCustomer().getId())
                .parkingSpotId(saved.getParkingSpot().getId())
                .build();
    }

    public void deleteById(Long id) {
        boolean reservationExists = reservationRepository.existsById(id);

        if (!reservationExists) {
            throw new NotFoundException("reservation", id);
        }

        reservationRepository.deleteById(id);
    }

    public List<ReservationDto> getAllByCustomerId(Long customerId) {
        boolean customerExists = customerRepository.existsById(customerId);

        if (!customerExists) {
            throw new NotFoundException("customer", customerId);
        }

        return reservationRepository.findAllByCustomerId(customerId)
                .stream()
                .map(r -> ReservationDto.builder()
                        .id(r.getId())
                        .customerId(r.getCustomer().getId())
                        .parkingSpotId(r.getParkingSpot().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
