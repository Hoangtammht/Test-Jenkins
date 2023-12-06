package com.eparking.eparking.domain.response;

import com.eparking.eparking.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCart {
    private int userID;
    private int carsNumber;
    private List<Reservation> reservationDetail;
}
