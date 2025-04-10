package com.example.focusflowbackend.services.payment;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MomoPaymentService {

    @Value("${momo.partner.code}")
    private String partnerCode;

    @Value("${momo.access.key}")
    private String accessKey;

    @Value("${momo.secret.key}")
    private String secretKey;

    @Value("${momo.api.endpoint}")
    private String apiEndpoint;

    @Value("${app.domain}")
    private String appDomain;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MomoPaymentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> createPaymentRequest(String orderId, Long amount, String orderInfo) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        
        String requestId = UUID.randomUUID().toString();
        String returnUrl = appDomain + "/api/payment/momo/return";
        String notifyUrl = appDomain + "/api/payment/momo/notify";
        String extraData = "";

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("partnerCode", partnerCode);
        requestData.put("accessKey", accessKey);
        requestData.put("requestId", requestId);
        requestData.put("amount", amount);
        requestData.put("orderId", orderId);
        requestData.put("orderInfo", orderInfo);
        requestData.put("returnUrl", returnUrl);
        requestData.put("notifyUrl", notifyUrl);
        requestData.put("extraData", extraData);
        requestData.put("requestType", "captureMoMoWallet");

        String rawSignature = "accessKey=" + accessKey + 
                "&amount=" + amount + 
                "&extraData=" + extraData + 
                "&orderId=" + orderId + 
                "&orderInfo=" + orderInfo + 
                "&partnerCode=" + partnerCode + 
                "&requestId=" + requestId + 
                "&returnUrl=" + returnUrl;

        String signature = createSignature(rawSignature, secretKey);
        requestData.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestData), headers);
        
        String response = restTemplate.postForObject(apiEndpoint, entity, String.class);
        
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        return responseMap;
    }

    public boolean verifyIpnSignature(Map<String, String> requestParams) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        
        String rawSignature = "accessKey=" + accessKey + 
                "&amount=" + requestParams.get("amount") + 
                "&extraData=" + requestParams.get("extraData") + 
                "&message=" + requestParams.get("message") + 
                "&orderId=" + requestParams.get("orderId") + 
                "&orderInfo=" + requestParams.get("orderInfo") + 
                "&orderType=" + requestParams.get("orderType") + 
                "&partnerCode=" + partnerCode + 
                "&payType=" + requestParams.get("payType") + 
                "&requestId=" + requestParams.get("requestId") + 
                "&responseTime=" + requestParams.get("responseTime") + 
                "&resultCode=" + requestParams.get("resultCode") + 
                "&transId=" + requestParams.get("transId");

        String signature = createSignature(rawSignature, secretKey);
        return signature.equals(requestParams.get("signature"));
    }

    private String createSignature(String data, String secretKey) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] hashBytes = hmacSha256.doFinal(data.getBytes("UTF-8"));
        
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
} 