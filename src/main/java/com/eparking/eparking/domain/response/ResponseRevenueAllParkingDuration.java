package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRevenueAllParkingDuration {
    private ResponseUserRegister supplierInfo;
    private Instant from;
    private Instant to;
    private double revenue;

}
