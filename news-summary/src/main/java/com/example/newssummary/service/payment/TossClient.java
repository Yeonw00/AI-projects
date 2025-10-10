package com.example.newssummary.service.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.dto.TossPaymentResponse;


@Component
public class TossClient {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${payments.toss.secret-key}")
	private String secretKey;

	public TossPaymentResponse confirm(String paymentKey, String orderId, long amount) {
		String url = "https://api.tosspayments.com/v1/payments/confirm";
		
		System.out.printf("Toss secretKey prefix: {}, len: {}", secretKey.substring(0, 7), secretKey.length());

		
		HttpHeaders headers = new HttpHeaders();
	    headers.setBasicAuth(secretKey, "");
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TossPaymentResponse> res =
                    restTemplate.postForEntity(url, request, TossPaymentResponse.class);
            return res.getBody();
        } catch (HttpStatusCodeException e) {
            // Toss 에러 바디를 로깅/전달하기 용이
            throw new IllegalStateException("Toss confirm failed: " + e.getResponseBodyAsString(), e);
        }
	}
}
