package com.eparking.eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parking {
    private int parkingID;
    private int methodID;
    private String parkingName;
    private String description;
    private String images;
    private String address;
    private double latitude;
    private double longitude;
    private int pricing;
    private int park;
    private int status;

}
