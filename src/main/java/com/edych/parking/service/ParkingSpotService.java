package com.edych.parking.service;

import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.service.dto.ParkingSpotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ReservationRepository reservationRepository;

    public List<ParkingSpotDto> getAllAvailable() {
        List<ParkingSpotDto> allTaken = new ArrayList<>();
        List<ParkingSpotDto> all = new ArrayList<>();

        parkingSpotRepository.findAll().forEach(parkingSpot ->
                all.add(ParkingSpotDto.builder()
                .id(parkingSpot.getId())
                .number(parkingSpot.getNumber())
                .floor(parkingSpot.getFloor())
                .handicapped(parkingSpot.getHandicapped())
                .build()
        ));

        reservationRepository.findAll().forEach(reservation ->
                allTaken.add(
                ParkingSpotDto.builder()
                        .id(reservation.getParkingSpot().getId())
                        .number(reservation.getParkingSpot().getNumber())
                        .floor(reservation.getParkingSpot().getFloor())
                        .handicapped(reservation.getParkingSpot().getHandicapped())
                        .build()
        ));

        return all.stream()
                .filter(parkingSpotDto -> !allTaken.contains(parkingSpotDto))
                .collect(Collectors.toList());
    }
}
