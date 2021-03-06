package com.edych.parking.repository;

import com.edych.parking.model.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    boolean existsByParkingSpotId(Long parkingSpotId);

    List<Reservation> findAllByCustomerId(Long customerId);

    List<Reservation> findAll();
}
