package com.edych.parking.service;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.mapper.ParkingSpotDtoMapperImpl;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.util.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ParkingSpotService.class, ParkingSpotDtoMapperImpl.class})
class ParkingSpotServiceTest {

    @Autowired
    private ParkingSpotService parkingSpotService;

    @MockBean
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
