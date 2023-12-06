package com.eparking.eparking.dao;

import com.eparking.eparking.domain.response.ResponseHistoryPayment;
import com.eparking.eparking.domain.resquest.TransactionData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    void insertTransaction(TransactionData transactionData);
    List<ResponseHistoryPayment> getHistoryPayment(int userID);

}
