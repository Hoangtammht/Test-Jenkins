package com.eparking.eparking.domain.response;

import com.eparking.eparking.domain.CarDetail;
import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUser {
    private int userID;
    private String phoneNumber;
    private String fullName;
    private String identifyCard;
    private List<Role> roleName;
    private double balance;
    private List<ResponseCarDetail> carList;
    private List<ResponseParking> parkingList;
}
