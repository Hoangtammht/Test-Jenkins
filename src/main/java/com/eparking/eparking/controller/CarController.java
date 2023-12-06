package com.eparking.eparking.controller;

import com.eparking.eparking.dao.CarDetailMapper;
import com.eparking.eparking.domain.response.ResponseCarDetail;
import com.eparking.eparking.domain.response.ResponseCarInParking;
import com.eparking.eparking.domain.resquest.RequestCarID;
import com.eparking.eparking.domain.resquest.ResquestCar;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.CarDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {

    private final CarDetailService carDetailService;
    private final CarDetailMapper carDetailMapper;

    @PostMapping("/addCar")
    public ResponseEntity<ResponseCarDetail> addCar(@RequestBody ResquestCar resquestCar,
                                                          HttpServletResponse response,
                                                          HttpServletRequest request) {
        try {
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/car/addCar")
                    .toUriString());
            ResponseCarDetail addedCars = carDetailService.addCar(resquestCar.getLicensePlate());
            return ResponseEntity.created(uri).body(addedCars);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @DeleteMapping("/removeCar")
    public ResponseEntity<ResponseCarDetail> removeCar(@RequestBody RequestCarID requestCarID,
                          HttpServletResponse response,
                          HttpServletRequest request) {
        try {
            ResponseCarDetail carDetail = carDetailMapper.findCarDetailByCarID(requestCarID.getCarID());
            carDetailService.removeCar(requestCarID.getCarID());
            return ResponseEntity.ok(carDetail);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/showCarsInParkingByStatus")
    public ResponseEntity<Page<ResponseCarInParking>> showCarsInParkingByStatus(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam int status,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            Page<ResponseCarInParking> carInParkings = carDetailService.findCarsInParkingByStatus(status, page - 1, size);
            return ResponseEntity.ok(carInParkings);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
    @GetMapping("/getListCarByUser")
    public ResponseEntity<List<ResponseCarDetail>> getListCarByUserID(){
        try{
            return ResponseEntity.ok(carDetailService.findCarDetailByUserID());
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
