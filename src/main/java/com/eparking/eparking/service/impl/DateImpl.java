package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.DateMapper;
import com.eparking.eparking.domain.response.ResponseDate;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.DateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DateImpl implements DateService {

    private final DateMapper dateMapper;

    @Override
    public List<ResponseDate> getDateOfParking(int parkingID) {
        try {
            return dateMapper.getDateOfParking(parkingID);
        }catch (Exception e){
            throw new ApiRequestException("Fail to get Date of Parking" + e.getMessage());
        }
    }


}
