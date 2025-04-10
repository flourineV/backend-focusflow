package com.example.focusflowbackend.controllers;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.focusflowbackend.dto.payment.PaymentRequest;
import com.example.focusflowbackend.dto.payment.PaymentResponse;
import com.example.focusflowbackend.services.payment.MomoPaymentService;
import com.example.focusflowbackend.services.payment.VNPayService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    @Autowired
    private VNPayService vnpayService;

    // Tạo thanh toán MoMo
    @PostMapping("/momo/create")
    public ResponseEntity<PaymentResponse> createMomoPayment(@RequestBody PaymentRequest request) {
        try {
            String orderId = "ORDER-" + UUID.randomUUID().toString();
            
            Map<String, String> momoResponse = momoPaymentService.createPaymentRequest(
                orderId, 
                request.getAmount(), 
                request.getOrderInfo()
            );
            
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(true);
            response.setPaymentUrl(momoResponse.get("payUrl"));
            response.setOrderId(orderId);
            response.setMessage("Success");
            
            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException | JsonProcessingException e) {
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(false);
            response.setMessage("Failed to create MoMo payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Xử lý callback từ MoMo
    @GetMapping("/momo/return")
    public ResponseEntity<String> momoReturn(@RequestParam Map<String, String> params) {
        // Redirect người dùng về trang kết quả thanh toán
        return ResponseEntity.ok("Payment completed. You can close this page.");
    }

    // Xử lý IPN (Instant Payment Notification) từ MoMo
    @PostMapping("/momo/notify")
    public ResponseEntity<String> momoNotify(@RequestBody Map<String, String> requestBody) {
        try {
            if (momoPaymentService.verifyIpnSignature(requestBody)) {
                String orderId = requestBody.get("orderId");
                String transId = requestBody.get("transId");
                String resultCode = requestBody.get("resultCode");
                
                // Kiểm tra kết quả thanh toán
                if ("0".equals(resultCode)) {
                    // Thanh toán thành công, cập nhật trạng thái đơn hàng
                    // orderService.updatePaymentStatus(orderId, "PAID", transId);
                    System.out.println("Payment successful for order: " + orderId + ", transaction: " + transId);
                } else {
                    // Thanh toán thất bại
                    // orderService.updatePaymentStatus(orderId, "FAILED", transId);
                    System.out.println("Payment failed for order: " + orderId + ", result code: " + resultCode);
                }
                
                // Trả về response cho MoMo theo định dạng yêu cầu
                Map<String, String> response = new HashMap<>();
                response.put("partnerCode", requestBody.get("partnerCode"));
                response.put("accessKey", requestBody.get("accessKey"));
                response.put("requestId", requestBody.get("requestId"));
                response.put("orderId", orderId);
                response.put("errorCode", "0");
                response.put("message", "success");
                response.put("responseTime", String.valueOf(System.currentTimeMillis()));
                response.put("extraData", requestBody.get("extraData"));
                
                return ResponseEntity.ok("OK");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Tạo thanh toán VNPay
    @PostMapping("/vnpay/create")
    public ResponseEntity<PaymentResponse> createVnPayPayment(
            @RequestBody PaymentRequest request, 
            HttpServletRequest servletRequest) {
        try {
            String orderId = "ORDER-" + UUID.randomUUID().toString();
            String ipAddress = servletRequest.getRemoteAddr();
            
            String paymentUrl = vnpayService.createPaymentUrl(
                orderId, 
                request.getOrderInfo(), 
                request.getAmount(),
                ipAddress
            );
            
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(true);
            response.setPaymentUrl(paymentUrl);
            response.setOrderId(orderId);
            response.setMessage("Success");
            
            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException e) {
            PaymentResponse response = new PaymentResponse();
            response.setSuccess(false);
            response.setMessage("Failed to create VNPay payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Xử lý callback từ VNPay
    @GetMapping("/vnpay/return")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            if (vnpayService.verifyPaymentReturn(params)) {
                String vnpResponseCode = params.get("vnp_ResponseCode");
                String vnpTransactionStatus = params.get("vnp_TransactionStatus");
                String vnpTxnRef = params.get("vnp_TxnRef"); // Order ID
                String vnpTransactionNo = params.get("vnp_TransactionNo");
                
                if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                    // Thanh toán thành công
                    // orderService.updatePaymentStatus(vnpTxnRef, "PAID", vnpTransactionNo);
                    System.out.println("VNPay payment successful for order: " + vnpTxnRef);
                    return ResponseEntity.ok("Payment completed successfully. You can close this page.");
                } else {
                    // Thanh toán thất bại
                    // orderService.updatePaymentStatus(vnpTxnRef, "FAILED", vnpTransactionNo);
                    System.out.println("VNPay payment failed for order: " + vnpTxnRef + 
                            ", response code: " + vnpResponseCode + 
                            ", transaction status: " + vnpTransactionStatus);
                    return ResponseEntity.ok("Payment failed. Please try again.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
} 