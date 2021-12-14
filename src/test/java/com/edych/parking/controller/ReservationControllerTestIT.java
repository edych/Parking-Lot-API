package com.edych.parking.controller;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.BadRequestException;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.service.ReservationService;
import com.edych.parking.util.TestObjectFactory;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationController.class)
class ReservationControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    void createReservationWhenCustomerAndParkingSpotExistsAndParkingSpotIsNotTaken() throws Exception {
        // given
        final String url = "/reservation";
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        final ReservationDto returnedReservationDto = TestObjectFactory.reservationDto(1L, parkingSpot, customer);

        // when
        when(reservationService.create(reservationDto)).thenReturn(returnedReservationDto);

        // then
        mockMvc.perform(post(url).content(TestObjectFactory.asJsonString(reservationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(returnedReservationDto.getId()))
                .andExpect(jsonPath("$.customerId").value(returnedReservationDto.getCustomerId()))
                .andExpect(jsonPath("$.parkingSpotId").value(returnedReservationDto.getParkingSpotId()))
                .andExpect(status().isCreated());
    }

    @Test
    void throwBadRequestExceptionWhenRequestHasAnId() throws Exception {
        // given
        final String url = "/reservation";
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final ReservationDto reservationDto = TestObjectFactory.reservationDto(1L, parkingSpot, customer);

        // when
        final String expMessage = "a request to create a new Reservation cannot have an id";

        // then
        mockMvc.perform(post(url).content(TestObjectFactory.asJsonString(reservationDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                        .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void throwNotFoundExceptionWhenCustomerDoesNotExist() throws Exception {
        // given
        final String url = "/reservation";
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(2L, "jane");
        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        // when
        final NotFoundException expectedException = new NotFoundException("customer", customer.getId());
        when(reservationService.create(reservationDto)).thenThrow(expectedException);

        // then
        final String expMessage = "Resource [customer] with id [2] does not exist.";
        mockMvc.perform(post(url).content(TestObjectFactory.asJsonString(reservationDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void throwNotFoundExceptionWhenParkingSpotDoesNotExist() throws Exception {
        // given
        final String url = "/reservation";
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(31L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        // when
        final NotFoundException expectedException = new NotFoundException("parkingSpot", parkingSpot.getId());
        when(reservationService.create(reservationDto)).thenThrow(expectedException);

        // then
        final String expMessage = "Resource [parkingSpot] with id [31] does not exist.";
        mockMvc.perform(post(url).content(TestObjectFactory.asJsonString(reservationDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void throwNotConflictExceptionWhenParkingSpotIsTaken() throws Exception {
        // given
        final String url = "/reservation";
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);
        final String expMessage = "Parking spot id [1] is already taken";

        // when
        final ConflictException expectedException = new ConflictException(expMessage);
        when(reservationService.create(reservationDto)).thenThrow(expectedException);

        // then
        mockMvc.perform(post(url).content(TestObjectFactory.asJsonString(reservationDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConflictException))
                .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void shouldDeleteTheReservationWhenReservationExists() throws Exception {
        // given
        final Long id = 1L;
        final String url = "/reservation/" + id;

        // when
        doNothing().when(reservationService).deleteById(id);

        // then
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowNotFoundExceptionWhileDeletingWhenReservationDoesNotExist() throws Exception {
        // given
        final Long id = 1L;
        final String url = "/reservation/" + id;

        // when
        final NotFoundException expectedException = new NotFoundException("reservation", id);
        doThrow(expectedException).when(reservationService).deleteById(id);

        // then
        final String expMessage = "Resource [reservation] with id [1] does not exist.";

        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void shouldReturnJSONAndIsOkStatusWhenThereAreReservationsByCustomerId() throws Exception {
        // given
        final Long id = 1L;
        final String url = "/reservations?customerId=" + id;
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final ParkingSpot ps1 = TestObjectFactory.parkingSpot(1L);
        final ReservationDto r1 = TestObjectFactory.reservationDto(1L, ps1, customer);
        final List<ReservationDto> reservations = List.of(r1);

        // when
        when(reservationService.getAllByCustomerId(customer.getId())).thenReturn(reservations);

        // then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(reservations.get(0).getId()))
                .andExpect(jsonPath("$[0].customerId").value(reservations.get(0).getCustomerId()))
                .andExpect(jsonPath("$[0].parkingSpotId").value(reservations.get(0).getParkingSpotId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyJSONAndIsOkStatusWhenThereAreNoReservationsByCustomerIdButCustomerExists() throws Exception {
        // given
        final Long id = 1L;
        final String url = "/reservations?customerId=" + id;
        final Customer customer = TestObjectFactory.customer(1L, "edych");
        final List<ReservationDto> reservations = new ArrayList<>();

        // when
        when(reservationService.getAllByCustomerId(customer.getId())).thenReturn(reservations);

        // then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCustomerDoesNotExist() throws Exception {
        // given
        final Long id = 2L;
        final String url = "/reservations?customerId=" + id;

        // when
        final NotFoundException expectedException = new NotFoundException("customer", id);

        when(reservationService.getAllByCustomerId(id)).thenThrow(expectedException);

        // then
        final String expMessage = "Resource [customer] with id [2] does not exist.";

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(expMessage, result.getResolvedException().getMessage()));
    }
}
