package com.eparking.eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpecialDate {
    private int specialDateID;
    private int parkingID;
    private double offerSpecialDate;

}
