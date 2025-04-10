package com.example.focusflowbackend.dto.payment;

import lombok.Data;

@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private String paymentUrl;
    private String orderId;
} 