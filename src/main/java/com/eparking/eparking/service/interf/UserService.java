package com.eparking.eparking.service.interf;

import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.User;
import com.eparking.eparking.domain.UserRole;
import com.eparking.eparking.domain.response.ResponseUser;
import com.eparking.eparking.domain.response.ResponseUserRegister;
import com.eparking.eparking.domain.resquest.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User findUserByPhoneNumber(String phoneNumber);

    ResponseUser findResponseUserByPhone(String phoneNumber);

    ResponseUser updateUserByUserID(UpdateUser updateUser);

    ResponseUser createUser(RequestCreateUser user);

    ResponseUser getUserProfile();

    List<UserRole> getRoleByUserID();

    void updateWalletForUser(String responseCode,Double Balance);
    ResponseUserRegister findResponseUserRegisterByUserID(int userID);

    String forgotPassword(RequestForgotPassword requestForgotPassword) throws IOException;
    String confirmPassword(RequestConfirmOTP requestConfirmOTP) throws IOException;
    String updateNewPassword(RequestNewPassword password);
    String confirmOTP(RequestConfirmOTP RequestConfirmOTP) throws IOException;
}
