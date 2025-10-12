package com.example.newssummary.service.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
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
		if (secretKey == null || secretKey.isBlank()) {
			throw new IllegalStateException("Toss secret key is empty");
		}
		
		System.out.println("paymentKey: " + paymentKey + ", orderId: " + orderId + ", amount: " + amount);
		
		String url = "https://api.tosspayments.com/v1/payments/confirm";
		
		HttpHeaders headers = new HttpHeaders();
		String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
		headers.set("Authorization", "Basic " + encodedAuth);
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	    
	    Map<String, Object> body = Map.of(
	    		"paymentKey", paymentKey,
	    		"orderId", orderId,
	    		"amount", amount
	    );
	    
	    

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TossPaymentResponse> res =
                    restTemplate.postForEntity(url, request, TossPaymentResponse.class);
            return res.getBody();
        } catch (HttpStatusCodeException e) {
            // Toss 에러 바디를 로깅/전달하기 용이
        	String bodyStr = e.getResponseBodyAsString();
        	int status = e.getRawStatusCode();
            throw new IllegalStateException("toss confirm failed (" + status + "): " + bodyStr, e);
        }
	}
}
