package com.edych.parking.service;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    @Transactional(readOnly = true)
    public List<ParkingSpotDto> getAllAvailable() {
        final List<ParkingSpot> parkingSpots = parkingSpotRepository.getAllAvailable();

        return parkingSpots.stream()
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
