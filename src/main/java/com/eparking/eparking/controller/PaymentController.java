package com.eparking.eparking.controller;

import com.eparking.eparking.domain.response.ResponseHistoryPayment;
import com.eparking.eparking.domain.resquest.Payment;
import com.eparking.eparking.domain.resquest.TransactionData;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.paymenConfig.VNpayConfig;
import com.eparking.eparking.service.impl.PaymentService;
import com.eparking.eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.eparking.eparking.paymenConfig.VNpayConfig.vnp_Version;

@CrossOrigin
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService UserService;

    @PostMapping("/createPayment")
    public ResponseEntity<?> createPayment(HttpServletRequest req, @RequestBody Payment payment) throws UnsupportedEncodingException {
        try {
            return paymentService.createPayment(req, payment);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/returnPayment")
    public ResponseEntity<?> paymentReturn(
            HttpServletRequest request
    ) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNpayConfig.hashAllFields(fields);

        TransactionData transactionData = new TransactionData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        if (signValue.equals(request.getParameter("vnp_SecureHash"))) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                try {
                    LocalDateTime paymentDateTime = LocalDateTime.parse(request.getParameter("vnp_PayDate"), formatter);
                    transactionData.setUserID(Integer.parseInt(request.getParameter("vnp_OrderInfo")));
                    transactionData.setBankCode(request.getParameter("vnp_BankCode"));
                    transactionData.setVnp_Amount(Double.parseDouble(request.getParameter("vnp_Amount")) / 100);
                    transactionData.setVnp_TxnRef(request.getParameter("vnp_TxnRef"));
                    transactionData.setPaymentDateTime(paymentDateTime);
                    paymentService.insertTransaction(transactionData);
                }catch (ApiRequestException e){
                    return ResponseEntity.ok("Payment handled failed");
                }
                return ResponseEntity.ok("Payment handled successfully");
            } else {
                return ResponseEntity.ok("Payment handled failed");
            }
        } else {
            return ResponseEntity.ok("Payment handled failed 0");
        }

    }

    @PutMapping("/updateWallet")
    public void updateWallet(@RequestParam(value = "vpn_ResponseCode") String ResponseCode,
                             @RequestParam(value = "vpn_Amount") Double amount) {
        try {
            UserService.updateWalletForUser(ResponseCode, amount);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/IPN")
    public void handleIPN(HttpServletRequest request) {
        try {
            paymentService.handleIPN(request);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/historyPayment")
    public ResponseEntity<List<ResponseHistoryPayment>> getHistoryPayment(){
        try {
            List<ResponseHistoryPayment> responseHistoryPayment = paymentService.getHistoryPayment();
            return ResponseEntity.ok(responseHistoryPayment);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

}
