package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRevenueParkingDuration {
    private ResponseParking parkingInfo;
    private Instant from;
    private Instant to;
    private double revenue;

}
