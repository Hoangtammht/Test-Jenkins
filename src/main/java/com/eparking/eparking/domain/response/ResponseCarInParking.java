package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCarInParking {
    private ResponseUserRegister customerInfo;
    private String phoneNumber;
    private String licensePlate;
    private String statusName;
    private int methodID;
    private String methodName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDatetime;
    private int customerID;
    private int pricing;
    private long totalPrice;

}
