package com.eparking.eparking.controller;

import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@CrossOrigin
@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;
    @GetMapping("/parking/today/{parkingID}")
    public ResponseEntity<ResponseRevenueParking> getRevenueParkingToday(@PathVariable("parkingID") int parkingID,
                                                                         HttpServletResponse response,
                                                                         HttpServletRequest request) {
        try {
            ResponseRevenueParking responseRevenueParking = revenueService.getRevenueParkingToday(parkingID);
            return ResponseEntity.ok(responseRevenueParking);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/allParking/today")
    public ResponseEntity<ResponseRevenueAllParking> getRevenueAllParkingToday(
                                                                         HttpServletResponse response,
                                                                         HttpServletRequest request) {
        try {
            ResponseRevenueAllParking responseRevenueAllParking = revenueService.getRevenueAllParkingToday();
            return ResponseEntity.ok(responseRevenueAllParking);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/parking/cal")
    public ResponseEntity<ResponseRevenueParkingDuration> getRevenueParkingByDurationTime(
            @RequestParam int parkingID,
            @RequestParam Instant from,
            @RequestParam Instant to,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            ResponseRevenueParkingDuration responseRevenueParkingDuration = revenueService.getRevenueParkingByDurationTime(parkingID, from, to);
            return ResponseEntity.ok(responseRevenueParkingDuration);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/allParking/cal")
    public ResponseEntity<ResponseRevenueAllParkingDuration> getRevenueAllParkingByDurationTime(
            @RequestParam Instant from,
            @RequestParam Instant to,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            ResponseRevenueAllParkingDuration responseRevenueAllParkingDuration = revenueService.getRevenueAllParkingByDurationTime(from, to);
            return ResponseEntity.ok(responseRevenueAllParkingDuration);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
}
