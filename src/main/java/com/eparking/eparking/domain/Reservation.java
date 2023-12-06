package com.eparking.eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    private int reserveID;
    private int userID;
    private int parkingID;
    private int statusID;
    private int carID;
    private Timestamp startDateTime;
    private Timestamp endDatetime;
}
