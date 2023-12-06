package com.eparking.eparking.dao;

import com.eparking.eparking.domain.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CartMapper {
    List<Cart> getListCartByUserID (int userID);
}
