package com.edych.parking.service;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.CustomerRepository;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Transactional
    public ReservationDto create(final ReservationDto dto) {
        final Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("customer", dto.getCustomerId()));

        final ParkingSpot parkingSpot = parkingSpotRepository.findById(dto.getParkingSpotId())
                .orElseThrow(() -> new NotFoundException("parkingSpot", dto.getParkingSpotId()));

        final boolean spotTaken = reservationRepository.existsByParkingSpotId(dto.getParkingSpotId());

        if (spotTaken) {
            final String msg = String.format("Parking spot id [%s] is already taken", dto.getParkingSpotId());
            throw new ConflictException(msg);
        }

        final Reservation reservation = Reservation.builder()
                .customer(customer)
                .parkingSpot(parkingSpot)
                .build();

        final Reservation saved = reservationRepository.save(reservation);

        return ReservationDto.builder()
                .id(saved.getId())
                .customerId(saved.getCustomer().getId())
                .parkingSpotId(saved.getParkingSpot().getId())
                .build();
    }

    @Transactional
    public void deleteById(final Long id) {
        final boolean reservationExists = reservationRepository.existsById(id);

        if (!reservationExists) {
            throw new NotFoundException("reservation", id);
        }

        reservationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllByCustomerId(final Long customerId) {
        final boolean customerExists = customerRepository.existsById(customerId);

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
