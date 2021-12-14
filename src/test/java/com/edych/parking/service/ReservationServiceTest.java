package com.edych.parking.service;

import com.edych.parking.dto.ReservationDto;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.mapper.ReservationDtoMapperImpl;
import com.edych.parking.model.Customer;
import com.edych.parking.model.ParkingSpot;
import com.edych.parking.model.Reservation;
import com.edych.parking.repository.CustomerRepository;
import com.edych.parking.repository.ParkingSpotRepository;
import com.edych.parking.repository.ReservationRepository;
import com.edych.parking.util.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationService.class, ReservationDtoMapperImpl.class})
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private ParkingSpotRepository parkingSpotRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReservationWhenCustomerExistsParkingSpotExistsAndIsNotTaken() {
        // given
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");

        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);
        final Reservation reservation = TestObjectFactory.reservation(null, parkingSpot, customer);
        final Reservation savedReservation = TestObjectFactory.reservation(1L, parkingSpot, customer);

        // when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(false);
        when(reservationRepository.save(reservation)).thenReturn(savedReservation);

        final ReservationDto returnedReservationDto = reservationService.create(reservationDto);

        // then
        assertEquals(parkingSpot.getId(), returnedReservationDto.getParkingSpotId());
        assertEquals(savedReservation.getId(), returnedReservationDto.getId());
        assertEquals(customer.getId(), returnedReservationDto.getCustomerId());
    }

    @Test
    void shouldReturnExceptionWhenCustomerDoesNotExist() {
        // given
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");

        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        // when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        // then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotDoesNotExist() {
        // given
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");

        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        // when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.create(reservationDto);
        });

        // then
        assertEquals("Resource [parkingSpot] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenParkingSpotIsTaken() {
        // given
        final ParkingSpot parkingSpot = TestObjectFactory.parkingSpot(1L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");

        final ReservationDto reservationDto = TestObjectFactory.reservationDto(null, parkingSpot, customer);

        // when
        when(customerRepository.findById(reservationDto.getCustomerId())).thenReturn(Optional.of(customer));
        when(parkingSpotRepository.findById(reservationDto.getParkingSpotId())).thenReturn(Optional.of(parkingSpot));
        when(reservationRepository.existsByParkingSpotId(reservationDto.getParkingSpotId())).thenReturn(true);

        final ConflictException exception = assertThrows(ConflictException.class, () -> {
            reservationService.create(reservationDto);
        });

        // then
        assertEquals("Parking spot id [1] is already taken", exception.getMessage());
    }

    @Test
    void shouldDeleteByIdWhenReservationExists() {
        // given
        final Long id = 1L;
        final ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        // when
        when(reservationRepository.existsById(id)).thenReturn(true);
        reservationService.deleteById(id);

        // then
        verify(reservationRepository, times(1)).deleteById(argumentCaptor.capture());
        final Long capturedValue = argumentCaptor.getValue();
        assertEquals(id, capturedValue);
    }

    @Test
    void doNotDeleteByIdWhenReservationDoesNotExist() {
        // given
        final Long id = 1L;

        // when
        when(reservationRepository.existsById(id)).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.deleteById(id);
        });

        // then
        assertEquals("Resource [reservation] with id [1] does not exist.", exception.getMessage());
    }

    @Test
    void shouldReturnReservationsListByCustomerIdWhenCustomerExists() {
        // given
        final Long customerId = 1L;
        final ParkingSpot parkingSpot1 = TestObjectFactory.parkingSpot(1L);
        final ParkingSpot parkingSpot2 = TestObjectFactory.parkingSpot(2L);
        final Customer customer = TestObjectFactory.customer(1L, "edych");

        final Reservation reservation1 = TestObjectFactory.reservation(1L, parkingSpot1, customer);
        final Reservation reservation2 = TestObjectFactory.reservation(2L, parkingSpot2, customer);

        final List<Reservation> reservations = List.of(reservation1, reservation2);

        // when
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(reservationRepository.findAllByCustomerId(customerId)).thenReturn(reservations);

        final List<ReservationDto> reservationDtoList = reservationService.getAllByCustomerId(customerId);

        // then
        assertEquals(reservations.size(), reservationDtoList.size());
    }

    @Test
    void shouldNotReturnReservationsListByCustomerIdWhenCustomerDoesNotExist() {
        // given
        final Long customerId = 1L;

        // when
        when(customerRepository.existsById(customerId)).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.getAllByCustomerId(customerId);
        });

        // then
        assertEquals("Resource [customer] with id [1] does not exist.", exception.getMessage());
    }
}
