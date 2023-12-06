package com.eparking.eparking.dao;

import com.eparking.eparking.domain.response.ResponseDate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DateMapper {
    List<ResponseDate> getDateOfParking(int parkingID);

}
