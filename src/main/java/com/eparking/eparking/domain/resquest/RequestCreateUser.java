package com.eparking.eparking.domain.resquest;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateUser {
    @ApiModelProperty(example= "0934328819")
    private String phoneNumber;
    @ApiModelProperty(example= "123456")
    private String password;
    @ApiModelProperty(example = "John Doe")
    private String fullName;
    @ApiModelProperty(example = "111111111111")
    private String identifyCard;
    @ApiModelProperty(example = "0")
    private double balance;
    @ApiModelProperty(example = "[2]")
    private List<Integer> userRoles;
}
