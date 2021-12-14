package com.edych.parking.service;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.mapper.ReservationDtoMapper;
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

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ReservationDtoMapper reservationDtoMapper;

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

        return reservationDtoMapper.toDto(saved);
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

        final List<Reservation> allByCustomerId = reservationRepository.findAllByCustomerId(customerId);

        return reservationDtoMapper.toDtos(allByCustomerId);
    }
}
