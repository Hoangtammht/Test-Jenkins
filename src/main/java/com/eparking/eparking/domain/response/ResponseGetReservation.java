package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGetReservation {
    private int reserveID;
    private ResponseUserRegister userObject;
    private ResponseParking parkingObject;
    private String address;
    private int pricing;
    private int statusID;
    private Timestamp startDateTime;
    private Timestamp endDatetime;
    private ResponseCarDetail carObject;
    private int totalPrice;
}
