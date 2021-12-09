package com.edych.parking.util;

import com.edych.parking.model.ParkingSpot;

public class TestObjectFactory {

    public static ParkingSpot parkingSpot(final Long id) {
        return ParkingSpot.builder()
                .id(id)
                .floor(1)
                .handicapped(true)
                .build();
    }
}
