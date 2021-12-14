package com.edych.parking.mapper;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationDtoMapper {

    @Mapping(target = "customerId", expression = "java(reservation.getCustomer().getId())")
    @Mapping(target = "parkingSpotId", expression = "java(reservation.getParkingSpot().getId())")
    ReservationDto toDto(Reservation reservation);

    List<ReservationDto> toDtos(List<Reservation> reservation);
}
