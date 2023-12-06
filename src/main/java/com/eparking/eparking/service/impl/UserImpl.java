package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.CarDetailMapper;
import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.dao.RoleMapper;
import com.eparking.eparking.dao.UserMapper;
import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.User;
import com.eparking.eparking.domain.UserRole;
import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.domain.resquest.*;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserDetailsService, UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final ParkingMapper parkingMapper;
    private final CarDetailMapper carDetailMapper;
    private final RoleMapper roleMapper;
    private final ESMService esmService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByPhoneNumber(username);
        if(user == null){
                log.error("User not found in the database");
                throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<UserRole> userRole = roleService.findRoleByPhoneNumber(user.getPhoneNumber());
        for (UserRole roleName: userRole) {
            authorities.add(new SimpleGrantedAuthority(roleName.getRoleName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getPassword(), authorities);
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) {
        try {
            return userMapper.findUserByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to find user by phone number" + e.getMessage());
        }
    }


    @Override
    public ResponseUser findResponseUserByPhone(String phoneNumber) {
        try {
            return userMapper.findResponseUserByPhone(phoneNumber);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to find response user by phone number" + e.getMessage());
        }
    }


    @Override
    public ResponseUser updateUserByUserID(UpdateUser updateUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phoneNumber = authentication.getName();
            User user = userMapper.findUserByPhoneNumber(phoneNumber);

            if (updateUser.getFullName() == null) {
                updateUser.setFullName(user.getFullName());
            }
            if (updateUser.getIdentifyCard() == null) {
                updateUser.setIdentifyCard(user.getIdentifyCard());
            }
            if (updateUser.getPassword() == null) {
                updateUser.setPassword(user.getPassword());
            }
            if (updateUser.getPhoneNumber() == null) {
                updateUser.setPhoneNumber(user.getPhoneNumber());
            }

            if (updateUser.getPhoneNumber() != null && !updateUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                User existingUser = userMapper.findUserByPhoneNumber(updateUser.getPhoneNumber());
                if (existingUser != null) {
                    throw new ApiRequestException("The user already exists");
                }
                user.setPhoneNumber(updateUser.getPhoneNumber());
            }

            if (updateUser.getPassword() != null) {
                updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            }

            userMapper.updateUserByUserID(updateUser, user.getUserID());

            List<ResponseCarDetail> carDetailList = carDetailMapper.findCarDetailByUserID(user.getUserID());
            List<ResponseParking> parkingList = parkingMapper.getListParkingByUserID(user.getUserID());
            List<Role> userRoles = roleMapper.findRoleForUser(user.getUserID());

            ResponseUser responseUser = new ResponseUser(
                    user.getUserID(),
                    updateUser.getPhoneNumber(),
                    updateUser.getFullName(),
                    updateUser.getIdentifyCard(),
                    userRoles,
                    user.getBalance(),
                    carDetailList,
                    parkingList
            );
            return responseUser;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update user by userID" + e.getMessage());
        }
    }


    @Override
    @Transactional
    public ResponseUser createUser(RequestCreateUser user) {
        User existingUser = userMapper.findUserByPhoneNumber(user.getPhoneNumber());
        ResponseUser responseUser = null;
        try {
            if (existingUser != null) {
                throw new ApiRequestException("The user is already exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userMapper.createSupplier(user,1);
            ResponseSendOTP responseSendOTP = esmService.sendOTP(user.getPhoneNumber());
            if(!responseSendOTP.getCodeResult().equals("100")){
                throw new ApiRequestException("Can not send OTP to user");
            }
            User newUser = userMapper.findUserByPhoneNumber(user.getPhoneNumber());
            for (Integer roleID : user.getUserRoles()) {
                roleService.insertUserRole(roleID, newUser.getUserID());
            }
            List<ResponseCarDetail> carDetailList = carDetailMapper.findCarDetailByUserID(newUser.getUserID());
            List<ResponseParking> parkingList = parkingMapper.getListParkingByUserID(newUser.getUserID());
            List<Role> userRoles = roleMapper.findRoleForUser(newUser.getUserID());
            responseUser = new ResponseUser(newUser.getUserID(), newUser.getPhoneNumber(), newUser.getFullName(), newUser.getIdentifyCard(),userRoles, newUser.getBalance(),carDetailList,parkingList);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create user: " + e.getMessage());
        }
        return responseUser;
    }

    @Override
    public void updateWalletForUser(String responseCode,Double Balance) {
        try{
            if(responseCode.equals("00")){
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = userMapper.findUserByPhoneNumber(authentication.getName());
                user.getBalance();
                if(user.getBalance() == 0.0 ){
                    user.setBalance(0.0);
                }
                Double userWallet = user.getBalance() + Balance;
                userMapper.updateWalletForUser(userMapper.findUserByPhoneNumber(authentication.getName()).getUserID(),userWallet);
            }else {
                throw new ApiRequestException("Failed to update wallet for user because responseCode invalid");
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to update wallet for user: " + e.getMessage());
        }
    }

    @Override
    public ResponseUser getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phoneNumber = authentication.getName();
            User user = findUserByPhoneNumber(phoneNumber);
            List<ResponseCarDetail> carDetailList = carDetailMapper.findCarDetailByUserID(user.getUserID());
            List<ResponseParking> parkingList = parkingMapper.getListParkingByUserID(user.getUserID());
            List<Role> userRoles = roleMapper.findRoleForUser(user.getUserID());
            ResponseUser responseUser = new ResponseUser(user.getUserID(), user.getPhoneNumber(), user.getFullName(), user.getIdentifyCard(), userRoles, user.getBalance(), carDetailList, parkingList);
            return responseUser;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get user profile : " + e.getMessage());
        }
    }

    @Override
    public List<UserRole> getRoleByUserID() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phoneNumber = authentication.getName();
            return roleService.findRoleByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get user roles" + e.getMessage());
        }
    }


    @Override
    public ResponseUserRegister findResponseUserRegisterByUserID(int userID) {
        try {
            ResponseUserRegister responseUserRegister = userMapper.findResponseUserRegisterByUserID(userID);
            List<Role> userRoles = roleMapper.findRoleForUser(userID);
            responseUserRegister.setRoleName(userRoles);
            return responseUserRegister;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to find response user register" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public String forgotPassword(RequestForgotPassword requestForgotPassword) throws IOException {
        try {
            User existingUser = userMapper.findUserByPhoneNumber(requestForgotPassword.getPhoneNumber());
            if (existingUser != null) {
                ResponseSendOTP responseSendOTP = esmService.sendOTP(requestForgotPassword.getPhoneNumber());
                if (!responseSendOTP.getCodeResult().equals("100")) {
                    throw new ApiRequestException("Can not send OTP to user");
                }
                return "User is exist";
            }
            return "This user is not exist";
        }catch (Exception e){
            throw new ApiRequestException("Failed to find user with phone number" + e.getMessage());
        }
    }

    @Override
    public String confirmPassword(RequestConfirmOTP requestConfirmOTP) throws IOException {
        try {
            ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestConfirmOTP.getPhoneNumber(), requestConfirmOTP.getOTP_code());
            if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                return "Successful";
            } else {
                return "OTP code is invalid";
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to confirm password" + e.getMessage());
        }
    }

    @Override
    public String confirmOTP(RequestConfirmOTP RequestConfirmOTP) throws IOException {
        try {
            ResponseCheckOTP responseCheckOTP = esmService.checkOTP(RequestConfirmOTP.getPhoneNumber(), RequestConfirmOTP.getOTP_code());
            if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                User user = userMapper.findUserByPhoneNumber(RequestConfirmOTP.getPhoneNumber());
                userMapper.updateStatusUser(user.getUserID(), 2);
                return "Successfully";
            } else {
                return "OTP code is invalid";
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to confirm OTP" + e.getMessage());
        }
    }

    @Override
    public String updateNewPassword(RequestNewPassword password) {
        try {
            User existingUser = userMapper.findUserByPhoneNumber(password.getPhoneNumber());
            if (existingUser != null) {
                String passwordEncoded = passwordEncoder.encode(password.getPassword());
                userMapper.updatePassword(existingUser.getUserID(), passwordEncoded);
                return "Recover successfully";
            }
            return "This user is not exist";
        }catch (Exception e){
            throw new ApiRequestException("Failed to update new password" + e.getMessage());
        }
    }
}
