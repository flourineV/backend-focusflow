package com.example.focusflowbackend.dto.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long amount;
    private String orderInfo;
    private String paymentMethod; // MOMO or VNPAY
    private String returnUrl; // Optional, client-side URL to redirect after payment
} 