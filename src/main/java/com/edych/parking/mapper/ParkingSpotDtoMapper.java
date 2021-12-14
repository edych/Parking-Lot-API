package com.edych.parking.mapper;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.model.ParkingSpot;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingSpotDtoMapper {

    ParkingSpotDto toDto(ParkingSpot parkingSpot);

    List<ParkingSpotDto> toDtos(List<ParkingSpot> parkingSpots);
}
