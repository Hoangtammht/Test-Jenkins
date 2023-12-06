package com.eparking.eparking.dao;

import com.eparking.eparking.domain.Reservation;
import com.eparking.eparking.domain.response.ResponseReservation;
import com.eparking.eparking.domain.resquest.RequestReservation;
import org.apache.ibatis.annotations.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper
public interface ReservationMapper {
    Reservation getReservationDetailByReservationID(int reserveID);

    List<ResponseReservation> getListOrderByUserAndStatusID(int userID, int statusID, int size, int offset);

    Long getNumberOfListOrder(int userID, int statusID);

    void createReservation(RequestReservation requestReservation, int userID);

    ResponseReservation getResponseReservationByReservationID(int reserveID);
    void updateStatus(int statusID,int reserveID);
    ResponseReservation getNewlyInsertedReservation(int userID);
    List<ResponseReservation> getOverlappingReservations(int parkingID, Instant startDateTime, Instant endDateTime);

}
