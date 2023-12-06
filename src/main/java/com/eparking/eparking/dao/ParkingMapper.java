package com.eparking.eparking.dao;

import com.eparking.eparking.domain.Parking;
import com.eparking.eparking.domain.ParkingDate;
import com.eparking.eparking.domain.ParkingSpecialDate;
import com.eparking.eparking.domain.SpecialDate;
import com.eparking.eparking.domain.response.ResponseParking;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Mapper
public interface ParkingMapper {
    void createParking(Parking parking, int userID);
    ResponseParking findParkingByParkingID(int parkingID);
    void addDatesForParking(List<ParkingDate> parkingDates);
    void addSpecialDatesForParking(List<ParkingSpecialDate> parkingSpecialDates);
    List<ParkingDate> showDatesOfParking(int parkingID);
    void createSpecialDate(SpecialDate specialDate);
    List<ResponseParking> getListParking(int size,long offset,int userID);
    long getNumberOfParkings();
    List<ResponseParking> searchNearbyParking(double latitude, double longitude, double radius, int size, long offset, String sortBy);

    List<ResponseParking> getListParkingByUserID(int userID);
    void updateParkForParking(int parkingID,int park);
    void updatePricingByParkingID(int parkingID,int pricing);

}
