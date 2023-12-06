package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.CarDetailMapper;
import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.domain.CarDetail;
import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.CarDetailService;
import com.eparking.eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarDetailImpl implements CarDetailService {

    private final CarDetailMapper carDetailMapper;
    private final UserService userService;
    private final ParkingMapper parkingMapper;
    @Override
    @Transactional
    public ResponseCarDetail addCar(String licensePlate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            CarDetail carDetail = new CarDetail();
            carDetail.setUserID(userID);
            carDetail.setLicensePlate(licensePlate);
            carDetailMapper.addCar(carDetail);
            return carDetailMapper.getNewlyCar();
        } catch (Exception e) {
            throw new ApiRequestException("Failed to add car: " + e.getMessage());
        }
    }


    @Override
    public void removeCar(int carID) {
        try {
            carDetailMapper.removeCar(carID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to remove car: " + e.getMessage());
        }
    }

    @Override
    public Page<ResponseCarInParking> findCarsInParkingByStatus(int status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            int offset = page * size;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            List<ResponseParking> listParkingOfUser = parkingMapper.getListParkingByUserID(userID);
            List<ResponseCarInParking> carsParking = new ArrayList<>();
            for (ResponseParking parking : listParkingOfUser) {
                List<ResponseCarInParking> carsInParking = carDetailMapper.findCarsInParkingByStatus(parking.getParkingID(), status, size, offset);
                for (ResponseCarInParking car : carsInParking) {
                    ResponseUserRegister customerInfo = userService.findResponseUserRegisterByUserID(car.getCustomerID());
                    car.setCustomerInfo(customerInfo);
                }
                carsParking.addAll(carsInParking);
            }
            long totalCount = carDetailMapper.getNumberOfReservationByStatus(status);
            return new PageImpl<>(carsParking, pageable, totalCount);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to find the list of cars by revenue status of the supplier: " + e.getMessage());
        }
    }


    @Override
    public List<ResponseCarDetail> findCarDetailByUserID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return carDetailMapper.findCarDetailByUserID(userService.findUserByPhoneNumber(authentication.getName()).getUserID());
        }catch (Exception e){
            throw new ApiRequestException("Failed to get list car by this user: " + e.getMessage());
        }
    }


}
