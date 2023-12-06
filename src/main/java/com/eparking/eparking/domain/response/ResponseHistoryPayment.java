package com.eparking.eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHistoryPayment {
    private String vnp_BankCode;
    private double vnp_Amount;
    private String vnp_TxnRef;
    private Instant paymentDatetime;

}
