package com.edych.parking.repository;

import com.edych.parking.dto.ParkingSpotDto;
import com.edych.parking.model.ParkingSpot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, Long> {

    List<ParkingSpot> findAll();

    @Query("SELECT ps from Reservation r RIGHT JOIN r.parkingSpot ps WHERE r.parkingSpot IS NULL")
    List<ParkingSpot> getAllAvailable();
}
