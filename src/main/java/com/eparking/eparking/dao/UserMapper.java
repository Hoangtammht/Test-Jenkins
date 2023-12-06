package com.eparking.eparking.dao;

import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.User;
import com.eparking.eparking.domain.UserRole;
import com.eparking.eparking.domain.response.RequestUpdatePricing;
import com.eparking.eparking.domain.response.ResponseUser;
import com.eparking.eparking.domain.response.ResponseUserRegister;
import com.eparking.eparking.domain.resquest.RequestCreateUser;
import com.eparking.eparking.domain.resquest.RequestUpdatestatus;
import com.eparking.eparking.domain.resquest.ResquestUpdateStatusUser;
import com.eparking.eparking.domain.resquest.UpdateUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findUserByPhoneNumber(String phoneNumber);
    ResponseUser findResponseUserByPhone(String phoneNumber);
    void updateUserByUserID(UpdateUser updateUser, int userID);
    void updateWalletForUser(int userID,Double balance);
    void createSupplier(RequestCreateUser user,int userstatus);
    User findUserByUserID (int userID);
    ResponseUserRegister findResponseUserRegisterByUserID(int userID);
    void updateStatusUser(int userID, int userstatus);
    void updatePassword(int userID, String password);
}
