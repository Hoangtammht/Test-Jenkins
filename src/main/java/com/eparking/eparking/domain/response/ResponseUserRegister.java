package com.eparking.eparking.domain.response;

import com.eparking.eparking.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserRegister {
    private String phoneNumber;
    private String fullName;
    private String identifyCard;
    private List<Role> roleName;
    private double balance;
}
