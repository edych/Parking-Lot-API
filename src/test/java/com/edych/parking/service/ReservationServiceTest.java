package com.edych.parking.service;

import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.CustomerRepository;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.dto.ReservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    ParkingSpotRepository parkingSpotRepository;

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

    private Reservation reservationFactory(Long id, ParkingSpot parkingSpot, Customer customer) {
        return Reservation.builder()
                .id(id)
                .customer(customer)
                .parkingSpot(parkingSpot)
                .build();
    }

    private ReservationDto reservationDtoFactory(Long id, ParkingSpot parkingSpot, Customer customer) {
        return ReservationDto.builder()
                .id(id)
                .customerId(customer.getId())
                .parkingSpotId(parkingSpot.getId())
                .build();
    }

    private Customer customerFactory(Long id, String name) {
        return Customer.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    void shouldCreateReservationWhenCustomerExistsParkingSpotExistsAndIsNotTaken() {
        //given
        ParkingSpot parkingSpot = parkingSpotFactory(1L);
        Customer customer = customerFactory(1L, "edych");

        ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);
        Reservation reservation = reservationFactory(null, parkingSpot, customer);
        Reservation savedReservation = reservationFactory(1L, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(false);
        when(reservationRepository.save(reservation)).thenReturn(savedReservation);

        ReservationDto returnedReservationDto = reservationService.create(reservationDto);

        //then
        assertEquals(parkingSpot.getId(), returnedReservationDto.getParkingSpotId());
        assertEquals(savedReservation.getId(), returnedReservationDto.getId());
        assertEquals(customer.getId(), returnedReservationDto.getCustomerId());
    }

    @Test
    void shouldReturnExceptionWhenCustomerDoesNotExist() {
        //given
        ParkingSpot parkingSpot = parkingSpotFactory(1L);
        Customer customer = customerFactory(1L, "edych");

        ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotDoesNotExist() {
        //given
        ParkingSpot parkingSpot = parkingSpotFactory(1L);
        Customer customer = customerFactory(1L, "edych");

        ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Resource [parkingSpot] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotIsTaken() {
        //given
        ParkingSpot parkingSpot = parkingSpotFactory(1L);
        Customer customer = customerFactory(1L, "edych");

        ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);
        Reservation reservation = reservationFactory(null, parkingSpot, customer);
        Reservation savedReservation = reservationFactory(1L, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Parking spot id [1] is already taken", exception.getMessage());
    }

    @Test
    void shouldDeleteByIdWhenReservationExists() {
        //given
        Long id = 1L;
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        //when
        when(reservationRepository.existsById(id)).thenReturn(true);
        reservationService.deleteById(id);

        //then
        verify(reservationRepository, times(1)).deleteById(argumentCaptor.capture());
        Long capturedValue = argumentCaptor.getValue();
        assertEquals(id, capturedValue);
    }

    @Test
    void doNotDeleteByIdWhenReservationDoesNotExist() {
        //given
        Long id = 1L;

        //when
        when(reservationRepository.existsById(id)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.deleteById(id);
        });

        //then
        assertEquals("Resource [reservation] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnReservationsListByCustomerIdWhenCustomerExists() {
        //given
        Long customerId = 1L;
        ParkingSpot parkingSpot1 = parkingSpotFactory(1L);
        ParkingSpot parkingSpot2 = parkingSpotFactory(2L);
        Customer customer = customerFactory(1L, "edych");

        Reservation reservation1 = reservationFactory(1L, parkingSpot1, customer);
        Reservation reservation2 = reservationFactory(2L, parkingSpot2, customer);

        List<Reservation> reservations = List.of(reservation1, reservation2);

        //when
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(reservationRepository.findAllByCustomerId(customerId)).thenReturn(reservations);

        List<ReservationDto> reservationDtoList = reservationService.getAllByCustomerId(customerId);

        //then
        assertEquals(reservations.size(), reservationDtoList.size());
    }

    @Test
    void shouldNotReturnReservationsListByCustomerIdWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;

        //when
        when(customerRepository.existsById(customerId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.getAllByCustomerId(customerId);
        });

        //then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }
}