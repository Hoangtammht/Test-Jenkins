package com.eparking.eparking.controller;

import com.eparking.eparking.dao.CarDetailMapper;
import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.dao.UserMapper;
import com.eparking.eparking.domain.Parking;
import com.eparking.eparking.domain.Reservation;
import com.eparking.eparking.domain.User;
import com.eparking.eparking.domain.response.*;
import com.eparking.eparking.domain.resquest.RequestReservation;
import com.eparking.eparking.domain.resquest.RequestUpdatestatus;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.ReservationService;
import com.eparking.eparking.service.interf.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;
    private final ParkingMapper parkingMapper;
    private final UserMapper userMapper;
    @GetMapping("/getListOrder")
    public ResponseEntity<Page<ResponseReservation>> getListParking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam int statusID) {
        try {
            Page<ResponseReservation> reservations = reservationService.getListOrderByUserAndStatusID(statusID,size,page);
            return ResponseEntity.ok(reservations);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @PostMapping("/createReservation")
    public ResponseEntity<?> createReservation(
            @RequestBody RequestReservation requestReservation,
            HttpServletResponse response,
            HttpServletRequest request){
        try {
            ResponseParking responseParking = parkingMapper.findParkingByParkingID(requestReservation.getParkingID());
            if(responseParking.getPark() == 0){
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Insufficient lot");
                jsonResponse.put("data", null);
                return ResponseEntity.ok(jsonResponse);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByPhoneNumber(authentication.getName());
            if(user.getBalance() - requestReservation.getTotalPrice() < 0){
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Insufficient money");
                jsonResponse.put("data", null);
                return ResponseEntity.ok(jsonResponse);
            }
            ResponseReservation createReservations = reservationService.createReservation(requestReservation);
            userMapper.updateWalletForUser(user.getUserID(),user.getBalance() - requestReservation.getTotalPrice());
            User userSup = userMapper.findUserByUserID(responseParking.getUserID());
            userMapper.updateWalletForUser(userSup.getUserID(), userSup.getBalance() + requestReservation.getTotalPrice());
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Reservation successful");
            jsonResponse.put("data", createReservations);
            return ResponseEntity.ok(jsonResponse);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
    @GetMapping("/getReservationByID/{reserveID}")
    public ResponseEntity<ResponseGetReservation> getReservationByID(@PathVariable int reserveID){
        try{
            ResponseGetReservation responseGetReservation = reservationService.getReservationByID(reserveID);
            return ResponseEntity.ok(responseGetReservation);
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<ResponseReservation> updateStatus(@RequestBody RequestUpdatestatus requestReservation){
        try{
            ResponseReservation responseReservation = reservationService.updateStatus(requestReservation.getStatusID(), requestReservation.getReserveID());
            return ResponseEntity.ok(responseReservation);

        }catch (ApiRequestException e){
            throw e;
        }
    }
}
