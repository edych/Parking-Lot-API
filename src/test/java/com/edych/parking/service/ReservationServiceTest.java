package com.edych.parking.service;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.CustomerRepository;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
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
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ParkingSpot parkingSpotFactory(final Long id) {
        return ParkingSpot.builder()
                .id(id)
                .floor(1)
                .handicapped(true)
                .build();
    }

    private Reservation reservationFactory(final Long id, final ParkingSpot parkingSpot, final Customer customer) {
        return Reservation.builder()
                .id(id)
                .customer(customer)
                .parkingSpot(parkingSpot)
                .build();
    }

    private ReservationDto reservationDtoFactory(final Long id, final ParkingSpot parkingSpot, final Customer customer) {
        return ReservationDto.builder()
                .id(id)
                .customerId(customer.getId())
                .parkingSpotId(parkingSpot.getId())
                .build();
    }

    private Customer customerFactory(final Long id, final String name) {
        return Customer.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    void shouldCreateReservationWhenCustomerExistsParkingSpotExistsAndIsNotTaken() {
        //given
        final ParkingSpot parkingSpot = parkingSpotFactory(1L);
        final Customer customer = customerFactory(1L, "edych");

        final ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);
        final Reservation reservation = reservationFactory(null, parkingSpot, customer);
        final Reservation savedReservation = reservationFactory(1L, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(false);
        when(reservationRepository.save(reservation)).thenReturn(savedReservation);

        final ReservationDto returnedReservationDto = reservationService.create(reservationDto);

        //then
        assertEquals(parkingSpot.getId(), returnedReservationDto.getParkingSpotId());
        assertEquals(savedReservation.getId(), returnedReservationDto.getId());
        assertEquals(customer.getId(), returnedReservationDto.getCustomerId());
    }

    @Test
    void shouldReturnExceptionWhenCustomerDoesNotExist() {
        //given
        final ParkingSpot parkingSpot = parkingSpotFactory(1L);
        final Customer customer = customerFactory(1L, "edych");

        final ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotDoesNotExist() {
        //given
        final ParkingSpot parkingSpot = parkingSpotFactory(1L);
        final Customer customer = customerFactory(1L, "edych");

        final ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Resource [parkingSpot] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotIsTaken() {
        //given
        final ParkingSpot parkingSpot = parkingSpotFactory(1L);
        final Customer customer = customerFactory(1L, "edych");

        final ReservationDto reservationDto = reservationDtoFactory(null, parkingSpot, customer);

        //when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(true);

        final ConflictException exception = assertThrows(ConflictException.class, () -> {
            reservationService.create(reservationDto);
        });

        //then
        assertEquals("Parking spot id [1] is already taken", exception.getMessage());
    }

    @Test
    void shouldDeleteByIdWhenReservationExists() {
        //given
        final Long id = 1L;
        final ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        //when
        when(reservationRepository.existsById(id)).thenReturn(true);
        reservationService.deleteById(id);

        //then
        verify(reservationRepository, times(1)).deleteById(argumentCaptor.capture());
        final Long capturedValue = argumentCaptor.getValue();
        assertEquals(id, capturedValue);
    }

    @Test
    void doNotDeleteByIdWhenReservationDoesNotExist() {
        //given
        final Long id = 1L;

        //when
        when(reservationRepository.existsById(id)).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.deleteById(id);
        });

        //then
        assertEquals("Resource [reservation] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnReservationsListByCustomerIdWhenCustomerExists() {
        //given
        final Long customerId = 1L;
        final ParkingSpot parkingSpot1 = parkingSpotFactory(1L);
        final ParkingSpot parkingSpot2 = parkingSpotFactory(2L);
        final Customer customer = customerFactory(1L, "edych");

        final Reservation reservation1 = reservationFactory(1L, parkingSpot1, customer);
        final Reservation reservation2 = reservationFactory(2L, parkingSpot2, customer);

        final List<Reservation> reservations = List.of(reservation1, reservation2);

        //when
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(reservationRepository.findAllByCustomerId(customerId)).thenReturn(reservations);

        final List<ReservationDto> reservationDtoList = reservationService.getAllByCustomerId(customerId);

        //then
        assertEquals(reservations.size(), reservationDtoList.size());
    }

    @Test
    void shouldNotReturnReservationsListByCustomerIdWhenCustomerDoesNotExist() {
        //given
        final Long customerId = 1L;

        //when
        when(customerRepository.existsById(customerId)).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.getAllByCustomerId(customerId);
        });

        //then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }
}