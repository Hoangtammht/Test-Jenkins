package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.dao.RevenueMapper;
import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.RevenueService;
import com.eparking.eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueImpl implements RevenueService {
    private final RevenueMapper revenueMapper;
    private final UserService userService;
    private final ParkingMapper parkingMapper;
    @Override
    public ResponseRevenueParking getRevenueParkingToday(int parkingID) {
        try {
            ResponseRevenueParking responseRevenueParking = new ResponseRevenueParking();
            ResponseParking responseParking = parkingMapper.findParkingByParkingID(parkingID);
            double revenue = revenueMapper.calculateParkingRevenueToday(parkingID);
            responseRevenueParking.setParkingInfo(responseParking);
            responseRevenueParking.setRevenue(revenue);
            return responseRevenueParking;
        }catch (Exception e){
            throw new ApiRequestException("Fail to get the revenue of parking today" + e.getMessage());
        }
    }

    @Override
    public ResponseRevenueAllParking getRevenueAllParkingToday() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            ResponseRevenueAllParking responseRevenueAllParking = new ResponseRevenueAllParking();
            ResponseUser responseUser = userService.getUserProfile();
            ResponseUserRegister supplierInfo = new ResponseUserRegister(responseUser.getPhoneNumber(), responseUser.getFullName(), responseUser.getIdentifyCard(), responseUser.getRoleName(),responseUser.getBalance());
            List<ResponseParking> parkingList = revenueMapper.getListParkingTodayOfUserID(userID);
            Set<Integer> uniqueParkingIDs = new HashSet<>();
            double totalRevenue = 0;
            for (ResponseParking parking : parkingList) {
                int parkingID = parking.getParkingID();
                if (!uniqueParkingIDs.contains(parkingID)) {
                    double parkingRevenue = revenueMapper.calculateParkingRevenueToday(parkingID);
                    totalRevenue += parkingRevenue;
                    uniqueParkingIDs.add(parkingID);
                }
            }
            responseRevenueAllParking.setSupplierInfo(supplierInfo);
            responseRevenueAllParking.setRevenue(totalRevenue);
            return responseRevenueAllParking;
        } catch (Exception e) {
            throw new ApiRequestException("Fail to get the revenue of all parking today" + e.getMessage());
        }
    }


    @Override
    public ResponseRevenueParkingDuration getRevenueParkingByDurationTime(int parkingID, Instant from, Instant to) {
        try {
            ResponseRevenueParkingDuration responseRevenueParkingDuration = new ResponseRevenueParkingDuration();
            ResponseParking responseParking = parkingMapper.findParkingByParkingID(parkingID);
            double revenue = revenueMapper.calculateParkingRevenueDurationTime(parkingID, from, to);
            responseRevenueParkingDuration.setParkingInfo(responseParking);
            responseRevenueParkingDuration.setFrom(from);
            responseRevenueParkingDuration.setTo(to);
            responseRevenueParkingDuration.setRevenue(revenue);
            return responseRevenueParkingDuration;
        }catch (Exception e){
            throw new ApiRequestException("Fail to get the revenue of parking duration time" + e.getMessage());
        }
    }

    public ResponseRevenueAllParkingDuration getRevenueAllParkingByDurationTime(Instant from, Instant to) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            ResponseRevenueAllParkingDuration responseRevenueAllParking = new ResponseRevenueAllParkingDuration();
            ResponseUser responseUser = userService.getUserProfile();
            ResponseUserRegister supplierInfo = new ResponseUserRegister(responseUser.getPhoneNumber(), responseUser.getFullName(), responseUser.getIdentifyCard(), responseUser.getRoleName(),responseUser.getBalance());
            List<ResponseParking> parkingList = revenueMapper.getListParkingDurationTimeOfUserID(userID, from, to);
            Set<Integer> uniqueParkingIDs = new HashSet<>();
            double totalRevenue = 0;
            for (ResponseParking parking : parkingList) {
                int parkingID = parking.getParkingID();
                if (!uniqueParkingIDs.contains(parkingID)) {
                    double parkingRevenue = revenueMapper.calculateParkingRevenueDurationTime(parkingID, from, to);
                    totalRevenue += parkingRevenue;
                    uniqueParkingIDs.add(parkingID);
                }
            }
            responseRevenueAllParking.setSupplierInfo(supplierInfo);
            responseRevenueAllParking.setFrom(from);
            responseRevenueAllParking.setTo(to);
            responseRevenueAllParking.setRevenue(totalRevenue);
            return responseRevenueAllParking;
        } catch (Exception e) {
            throw new ApiRequestException("Fail to get the revenue of all parking during the specified duration" + e.getMessage());
        }
    }

}
