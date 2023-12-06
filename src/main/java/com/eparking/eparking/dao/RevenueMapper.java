package com.eparking.eparking.dao;

import com.eparking.eparking.domain.response.ResponseParking;
import org.apache.ibatis.annotations.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper
public interface RevenueMapper {
    double calculateParkingRevenueToday(int parkingID);
    List<ResponseParking> getListParkingTodayOfUserID(int userID);
    double calculateParkingRevenueDurationTime(int parkingID, Instant from, Instant to);
    List<ResponseParking> getListParkingDurationTimeOfUserID(int userID, Instant from, Instant to);

}
