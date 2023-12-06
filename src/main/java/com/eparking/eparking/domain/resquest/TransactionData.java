package com.eparking.eparking.domain.resquest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionData {
    private int userID;
    private String bankCode;
    private double vnp_Amount;
    private String vnp_TxnRef;
    private LocalDateTime paymentDateTime;

}
