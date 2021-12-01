package com.edych.parking.service;

import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.service.dto.ParkingSpotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ReservationRepository reservationRepository;

    public List<ParkingSpotDto> getAllAvailable() {
        List<ParkingSpot> all = parkingSpotRepository.findAll();
        List<ParkingSpot> allTaken = reservationRepository.findAll().stream()
                .map(Reservation::getParkingSpot)
                .collect(Collectors.toList());

        return all.stream()
                .filter(spot -> !allTaken.contains(spot))
                .map(spot ->
                        ParkingSpotDto.builder()
                                .id(spot.getId())
                                .number(spot.getNumber())
                                .floor(spot.getFloor())
                                .handicapped(spot.getHandicapped())
                                .build()
                ).collect(Collectors.toList());
    }
}
