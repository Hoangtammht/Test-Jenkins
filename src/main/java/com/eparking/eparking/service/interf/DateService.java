package com.eparking.eparking.service.interf;

import com.eparking.eparking.domain.response.ResponseDate;

import java.util.List;

public interface DateService {
    List<ResponseDate> getDateOfParking(int parkingID);

}
