package com.eparking.eparking.service.interf;

import com.eparking.eparking.domain.SpecialDate;

import java.util.List;

public interface SpecialDateService {
    List<SpecialDate> getSpecialDateOfParking(int parkingID);

}
