package com.edych.parking.controller;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.service.ParkingSpotService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingSpotController.class)
class ParkingSpotControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingSpotService parkingSpotService;

    private ParkingSpotDto parkingSpotDtoFactory(final Long id) {
        return ParkingSpotDto.builder()
                .id(id)
                .floor(1)
                .handicapped(true)
                .build();
    }

    @Test
    void shouldReturnJSONAndIsOkStatusWhenThereAreParkingSpotsAvailable() throws Exception {
        // given
        final String url = "/parking-spot/available";

        final ParkingSpotDto p1 = parkingSpotDtoFactory(1L);
        final ParkingSpotDto p2 = parkingSpotDtoFactory(2L);
        final ParkingSpotDto p3 = parkingSpotDtoFactory(3L);
        final ParkingSpotDto p4 = parkingSpotDtoFactory(4L);

        final List<ParkingSpotDto> all = List.of(p1, p2, p3, p4);

        // when
        when(parkingSpotService.getAllAvailable()).thenReturn(all);

        // then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(all.size())));
    }

    @Test
    void shouldReturnEmptyJSONAndIsOkStatusWhenThereAreNoParkingSpotsAvailable() throws Exception {
        // given
        final String url = "/parking-spot/available";

        final List<ParkingSpotDto> all = new ArrayList<>();

        // when
        when(parkingSpotService.getAllAvailable()).thenReturn(all);

        // then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(all.size())));
    }
}
