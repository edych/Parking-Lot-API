package com.edych.parking.controller;

import com.edych.parking.service.ParkingSpotService;
import com.edych.parking.dto.ParkingSpotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @GetMapping("/parking-spot/available")
    public List<ParkingSpotDto> getAllAvailable() {
        return parkingSpotService.getAllAvailable();
    }
}
