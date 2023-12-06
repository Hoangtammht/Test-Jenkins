package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.PaymentMapper;
import com.eparking.eparking.dao.UserMapper;
import com.eparking.eparking.domain.User;
import com.eparking.eparking.domain.response.ResponseHistoryPayment;
import com.eparking.eparking.domain.resquest.Payment;
import com.eparking.eparking.domain.resquest.TransactionData;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.paymenConfig.VNpayConfig;
import com.eparking.eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.eparking.eparking.paymenConfig.VNpayConfig.vnp_Version;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final UserService userService;

    private final PaymentMapper paymentMapper;

    private final UserMapper userMapper;

    public ResponseEntity<?> createPayment(HttpServletRequest req, Payment payment) throws UnsupportedEncodingException {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            long amount =  Integer.parseInt(payment.getAmountParam()) * 100;
            String bankCode = "VNBANK";

            String vnp_TxnRef = VNpayConfig.getRandomNumber(8);
            String vnp_IpAddr = VNpayConfig.getIpAddress(req);
            String vnp_TmnCode = VNpayConfig.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");

            if (payment.getBackCode() == null && payment.getBackCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }else{
                vnp_Params.put("vnp_BankCode", payment.getBackCode());
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();

            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", String.valueOf(userID));
            vnp_Params.put("vnp_OrderType", "other");

            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNpayConfig.vnp_Returnurl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNpayConfig.hmacSHA512(VNpayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNpayConfig.vnp_PayUrl + "?" + queryUrl;

            Map<String, String> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("url", paymentUrl);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            throw new ApiRequestException("Fail to create payment action: " + e.getMessage());
        }
    }
    public ResponseEntity<String> handleIPN(HttpServletRequest request){
        try{
        Map<String, String> fields = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Check checksum
        String signValue = VNpayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in your database).
            boolean checkOrderStatus = true; // PaymentStatus = 0 (pending)
                    if (checkOrderStatus) {
                        if ("00".equals(request.getParameter("vnp_ResponseCode"))) {

                            userService.updateWalletForUser(request.getParameter("vnp_ResponseCode"),Double.valueOf(request.getParameter("vnp_Amount")));
                        } else {

                            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Something wrong here\"}");
                        }
                        return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                    } else {
                        return ResponseEntity.ok("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                    }
                } else {
                    return ResponseEntity.ok("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                }
            } catch (Exception e) {
                return ResponseEntity.ok("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
            }
        }

    public ResponseEntity<?> insertTransaction(TransactionData transactionData) {
        try {
            paymentMapper.insertTransaction(transactionData);
            User user = userMapper.findUserByUserID(transactionData.getUserID());
            double userWallet = user.getBalance() + transactionData.getVnp_Amount();
            userMapper.updateWalletForUser(transactionData.getUserID(), userWallet);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to insert transaction: " + e.getMessage());
        }
    }


    public List<ResponseHistoryPayment> getHistoryPayment() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            List<ResponseHistoryPayment> responseHistoryPayment = paymentMapper.getHistoryPayment(userID);
            return responseHistoryPayment;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create parking: " + e.getMessage());
        }
    }
}
