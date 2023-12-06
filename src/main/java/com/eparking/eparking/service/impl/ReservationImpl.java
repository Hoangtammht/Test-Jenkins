package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.CarDetailMapper;
import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.dao.ReservationMapper;
import com.eparking.eparking.domain.Parking;
import com.eparking.eparking.domain.Reservation;
import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.domain.resquest.RequestReservation;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.ReservationService;
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

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final ParkingMapper parkingMapper;
    private final CarDetailMapper carDetailMapper;
    @Override
    public Reservation getReservationDetailByReservationID(int reserveID) {
        try{
            return reservationMapper.getReservationDetailByReservationID(reserveID);
        }catch (Exception e){
            throw new ApiRequestException("Fail to get detail reservation: " + e.getMessage());
        }
    }

    @Override
    public Page<ResponseReservation> getListOrderByUserAndStatusID(int statusID, int size, int page) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            Pageable pageable = PageRequest.of(page, size);
            int offset = (page-1) * size;
            List<ResponseReservation> RepoReservation = reservationMapper.getListOrderByUserAndStatusID(userID,statusID,size,offset);
            Long totalCount = reservationMapper.getNumberOfListOrder(userID,statusID);
            return new PageImpl<>(RepoReservation,pageable,totalCount);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get the list order by this user: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseReservation createReservation(RequestReservation requestReservation) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            List<ResponseReservation> overlappingReservations = reservationMapper.getOverlappingReservations(
                    requestReservation.getParkingID(),
                    requestReservation.getStartDateTime(),
                    requestReservation.getEndDatetime()
            );
            ResponseParking responseParking = parkingMapper.findParkingByParkingID(requestReservation.getParkingID());
            int availableParkingSpaces = responseParking.getPark() - overlappingReservations.size();
            if (availableParkingSpaces >= 1) {
                reservationMapper.createReservation(requestReservation, userID);
                return reservationMapper.getNewlyInsertedReservation(userID);
            } else {
                throw new ApiRequestException("No available parking spaces in the requested period.");
            }
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create reservation: " + e.getMessage());
        }
    }


    @Override
    public ResponseReservation updateStatus(int statusID, int reserveID) {
        try{
            reservationMapper.updateStatus(statusID,reserveID);
            return reservationMapper.getResponseReservationByReservationID(reserveID);
        }catch (Exception e){
            throw new ApiRequestException("Failed to update status reservation: " + e.getMessage());
        }
    }

    @Override
    public ResponseGetReservation getReservationByID(int reserveID) {
        try {
            ResponseReservation reservation = getResponseReservationByReservationID(reserveID);
            ResponseUser responseUser = userService.getUserProfile();
            ResponseParking responseParking = parkingMapper.findParkingByParkingID(reservation.getParkingID());
            ResponseCarDetail carDetail = carDetailMapper.findCarDetailByCarID(reservation.getCarID());
            ResponseUserRegister responseUserRegister = new ResponseUserRegister(responseUser.getPhoneNumber(), responseUser.getFullName(), responseUser.getIdentifyCard(), responseUser.getRoleName(), responseUser.getBalance());
            ResponseGetReservation responseGetReservation = new ResponseGetReservation(reservation.getReserveID(), responseUserRegister, responseParking, reservation.getAddress(), reservation.getPricing(), reservation.getStatusID(), reservation.getStartDateTime(), reservation.getEndDatetime(), carDetail, reservation.getTotalPrice());
            return responseGetReservation;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get reservation by ID: " + e.getMessage());
        }
    }

    @Override
    public ResponseReservation getResponseReservationByReservationID(int reserveID) {
        try{
            ResponseReservation reservation = reservationMapper.getResponseReservationByReservationID(reserveID);
            if(reservation==null){
                throw new ApiRequestException("Not have any reservation with this ID");
            }
            return reservation;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get reservation by this ID: " + e.getMessage());
        }
    }
}
