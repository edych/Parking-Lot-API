package com.edych.parking.service;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.mapper.ParkingSpotDtoMapper;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSpotDtoMapper parkingSpotDtoMapper;

    @Transactional(readOnly = true)
    public List<ParkingSpotDto> getAllAvailable() {
        final List<ParkingSpot> parkingSpots = parkingSpotRepository.getAllAvailable();
        return parkingSpotDtoMapper.toDtos(parkingSpots);
    }
}
