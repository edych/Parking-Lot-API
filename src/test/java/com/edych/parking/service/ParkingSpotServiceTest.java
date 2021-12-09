package com.edych.parking.service;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.util.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ParkingSpotServiceTest {

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAvailableParkingSpotsDtoWhenParkingSpotRepositoryReturnsNotEmptyParkingSpotList() {
        // given
        final ParkingSpot p1 = TestObjectFactory.parkingSpot(1L);
        final ParkingSpot p2 = TestObjectFactory.parkingSpot(2L);
        final ParkingSpot p3 = TestObjectFactory.parkingSpot(3L);
        final ParkingSpot p4 = TestObjectFactory.parkingSpot(4L);

        final List<ParkingSpot> all = List.of(p1, p2, p3, p4);

        // when
        when(parkingSpotRepository.getAllAvailable()).thenReturn(all);

        final List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(all.size(), allAvailable.size());
        verify(parkingSpotRepository, times(1)).getAllAvailable();
    }

    @Test
    void shouldNotReturnAvailableParkingSpotsDtoWhenParkingSpotRepositoryReturnsEmptyParkingSpotList() {
        // given
        final List<ParkingSpot> all = new ArrayList<>();

        // when
        when(parkingSpotRepository.getAllAvailable()).thenReturn(all);

        final List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(0, allAvailable.size());
        verify(parkingSpotRepository, times(1)).getAllAvailable();
    }
}