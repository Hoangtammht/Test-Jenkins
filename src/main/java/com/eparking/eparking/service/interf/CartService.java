package com.eparking.eparking.service.interf;

import com.eparking.eparking.domain.Cart;
import com.eparking.eparking.domain.response.ResponseCart;

import java.util.List;

public interface CartService {
    List<Cart> getListCartByUserID ();
    ResponseCart getListReservation();
}
