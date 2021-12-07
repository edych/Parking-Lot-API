package com.edych.parking.service;

import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.service.dto.ParkingSpotDto;
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
    ParkingSpotService parkingSpotService;

    @Mock
    ParkingSpotRepository parkingSpotRepository;

    @Mock
    ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ParkingSpot parkingSpotFactory(Long id) {
        return ParkingSpot.builder()
                .id(id)
                .floor(1)
                .handicapped(true)
                .build();
    }

    private Reservation reservationFactory(Long id, ParkingSpot parkingSpot) {
        return Reservation.builder()
                .id(id)
                .parkingSpot(parkingSpot)
                .build();
    }

    @Test
    void shouldReturnAvailableParkingSpotsWhenFewAreTaken() {
        // given
        ParkingSpot p1 = parkingSpotFactory(1L);
        ParkingSpot p2 = parkingSpotFactory(2L);
        ParkingSpot p3 = parkingSpotFactory(3L);
        ParkingSpot p4 = parkingSpotFactory(4L);

        Reservation r1 = reservationFactory(1L, p1);
        Reservation r2 = reservationFactory(2L, p2);

        List<ParkingSpot> all = List.of(p1, p2, p3, p4);
        List<Reservation> reservations = List.of(r1, r2);

        // when
        when(parkingSpotRepository.findAll()).thenReturn(all);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(2, allAvailable.size());

        verify(parkingSpotRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAllParkingSpotsWhenThereIsNoReservation() {
        // given
        ParkingSpot p1 = parkingSpotFactory(1L);
        ParkingSpot p2 = parkingSpotFactory(2L);
        ParkingSpot p3 = parkingSpotFactory(3L);

        List<ParkingSpot> all = List.of(p1, p2, p3);
        List<Reservation> reservations = new ArrayList<>();

        // when
        when(parkingSpotRepository.findAll()).thenReturn(all);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(3, allAvailable.size());

        verify(parkingSpotRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoParkingSpots() {
        // given
        List<ParkingSpot> all = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();

        // when
        when(parkingSpotRepository.findAll()).thenReturn(all);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(0, allAvailable.size());

        verify(parkingSpotRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereIsNoParkingSpotsAvailable() {
        // given
        ParkingSpot p1 = parkingSpotFactory(1L);
        ParkingSpot p2 = parkingSpotFactory(2L);

        Reservation r1 = reservationFactory(1L, p1);
        Reservation r2 = reservationFactory(2L, p2);

        List<ParkingSpot> all = List.of(p1, p2);
        List<Reservation> reservations = List.of(r1, r2);

        // when
        when(parkingSpotRepository.findAll()).thenReturn(all);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ParkingSpotDto> allAvailable = parkingSpotService.getAllAvailable();

        // then
        assertEquals(0, allAvailable.size());

        verify(parkingSpotRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findAll();
    }
}