package com.eparking.eparking.service.interf;

import com.eparking.eparking.domain.response.ResponseRevenueAllParking;
import com.eparking.eparking.domain.response.ResponseRevenueAllParkingDuration;
import com.eparking.eparking.domain.response.ResponseRevenueParking;
import com.eparking.eparking.domain.response.ResponseRevenueParkingDuration;

import java.time.Instant;

public interface RevenueService {
    ResponseRevenueParking getRevenueParkingToday(int parkingID);
    ResponseRevenueAllParking getRevenueAllParkingToday();
    ResponseRevenueParkingDuration getRevenueParkingByDurationTime(int parkingID, Instant from, Instant to);
    ResponseRevenueAllParkingDuration getRevenueAllParkingByDurationTime(Instant from, Instant to);
}
