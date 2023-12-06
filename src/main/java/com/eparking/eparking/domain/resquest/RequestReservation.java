package com.eparking.eparking.domain.resquest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestReservation {
    private int parkingID;
    private Instant startDateTime;
    private Instant endDatetime;
    private double totalPrice;
    private int statusID;
    private int carID;

}
