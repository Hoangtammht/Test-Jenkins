package com.eparking.eparking.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import com.eparking.eparking.service.impl.ESMService;
import com.eparking.eparking.service.interf.RoleService;
import com.eparking.eparking.service.interf.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final RoleMapper roleMapper;
    private final CarDetailMapper carDetailMapper;
    private final ParkingMapper parkingMapper;
    private final ESMService esmService;
    private final UserMapper userMapper;
    @Value("${SECRET_KEY}")
    private String secret;

    @PostMapping("/loginUser")
    public void loginUser(@RequestBody LoginUser loginUser,
                          HttpServletResponse response,
                          HttpServletRequest request) throws IOException {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getPhoneNumber(), loginUser.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            User user = userService.findUserByPhoneNumber(name);
            if(user.getUserstatus() == 2) {
                List<UserRole> userRole = roleService.findRoleByPhoneNumber(user.getPhoneNumber());
                List<Role> userRoleRe = roleMapper.findRoleForUser(user.getUserID());
                List<String> roleNames = new ArrayList<>();
                for (UserRole role : userRole) {
                    roleNames.add(role.getRoleName());
                }
                String access_token = JWT.create()
                        .withSubject(user.getPhoneNumber())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", roleNames)
                        .sign(algorithm);
                ResponseUser responseUser = userService.findResponseUserByPhone(user.getPhoneNumber());
                responseUser.setRoleName(userRoleRe);
                List<ResponseCarDetail> carDetailList = carDetailMapper.findCarDetailByUserID(user.getUserID());
                List<ResponseParking> parkingList = parkingMapper.getListParkingByUserID(user.getUserID());
                responseUser.setCarList(carDetailList);
                responseUser.setParkingList(parkingList);
                Map<String, Object> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("User", responseUser);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
            else {
                throw new ApiRequestException("This user is denied to login");
            }
        }catch (Exception e){
            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ResponseUser> updateUser(@RequestBody UpdateUser updateUser,
                                           HttpServletResponse response,
                                           HttpServletRequest request) {
        try {
            ResponseUser updatedUser = userService.updateUserByUserID(updateUser);
            return ResponseEntity.ok().body(updatedUser);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @PostMapping("/registerUser")
    public ResponseEntity<ResponseUserRegister> createUser(@RequestBody RequestCreateUser user,
                                                           HttpServletResponse response,
                                                           HttpServletRequest request) {
        try {
            ResponseUser newUserRegister = userService.createUser(user);
            ResponseUserRegister responseUserRegister = new ResponseUserRegister(newUserRegister.getPhoneNumber(), newUserRegister.getFullName(), newUserRegister.getIdentifyCard(), newUserRegister.getRoleName(),newUserRegister.getBalance());
            return ResponseEntity.ok().body(responseUserRegister);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/getUserProfile")
    public ResponseEntity<ResponseUser> getUserProfile(HttpServletResponse response, HttpServletRequest request) {
        try {
            ResponseUser userProfile = userService.getUserProfile();
            return ResponseEntity.ok().body(userProfile);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/getRoleByUserID")
    public ResponseEntity<List<UserRole>> getRoleByUserID(HttpServletResponse response, HttpServletRequest request){
        try {
            List<UserRole> listRole = userService.getRoleByUserID();
            return ResponseEntity.ok().body(listRole);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
    @PutMapping("/confirmOTP")
    public ResponseEntity<?> updateStatusUser(@RequestBody RequestConfirmOTP requestConfirmOTP) {
        try{
           return ResponseEntity.ok(userService.confirmOTP(requestConfirmOTP));
        }catch (ApiRequestException e){
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody RequestForgotPassword requestForgotPassword){
        try{
            return ResponseEntity.ok(userService.forgotPassword(requestForgotPassword));
        }catch (ApiRequestException e){
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/confirmOTP")
    public ResponseEntity<String> confirmOTPassword(@RequestBody RequestConfirmOTP requestConfirmOTP) throws IOException {
        try {
            return ResponseEntity.ok(userService.confirmPassword(requestConfirmOTP));
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/newPasword")
    public ResponseEntity<String> updateNewPassword(@RequestBody RequestNewPassword newPasswor){
        try {
            return ResponseEntity.ok(userService.updateNewPassword(newPasswor));
        }catch (Exception e){
            throw e;
        }
    }
}
