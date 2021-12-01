package com.edych.parking.repository;

import com.edych.parking.model.ParkingSpot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, Long> {

    List<ParkingSpot> findAll();
}
