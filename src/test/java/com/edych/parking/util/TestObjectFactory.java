package com.edych.parking.util;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestObjectFactory {

    public static ParkingSpot parkingSpot(final Long id) {
        return ParkingSpot.builder()
                .id(id)
                .number(id.intValue())
                .floor(1)
                .handicapped(true)
                .build();
    }

    public static ReservationDto reservationDto(final Long id, final ParkingSpot parkingSpot, final Customer customer) {
        return ReservationDto.builder()
                .id(id)
                .customerId(customer.getId())
                .parkingSpotId(parkingSpot.getId())
                .build();
    }

    public static Customer customer(final Long id, final String name) {
        return Customer.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    public static Reservation reservation(final Long id, final ParkingSpot parkingSpot, final Customer customer) {
        return Reservation.builder()
                .id(id)
                .customer(customer)
                .parkingSpot(parkingSpot)
                .build();
    }
}
