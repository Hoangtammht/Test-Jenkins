package com.eparking.eparking.dao;

import com.eparking.eparking.domain.SpecialDate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpecialDateMapper {
    List<SpecialDate> getSpecialDateOfParking(int parkingID);

}
