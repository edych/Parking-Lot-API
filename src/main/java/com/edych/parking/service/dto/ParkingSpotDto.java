package com.edych.parking.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotDto {

    private Long id;
    private Integer number;
    private Integer floor;
    private Boolean handicapped;
}
