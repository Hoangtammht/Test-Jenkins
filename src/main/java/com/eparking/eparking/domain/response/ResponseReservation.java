package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseReservation {
    private int reserveID;
    private int userID;
    private int parkingID;
    private String parkingName;
    private String address;
    private int pricing;
    private int statusID;
    private Timestamp startDateTime;
    private Timestamp endDatetime;
    private int carID;
    private String licensePlate;
    private int totalPrice;

}
