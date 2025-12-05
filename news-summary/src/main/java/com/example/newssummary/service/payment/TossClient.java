package com.example.newssummary.service.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.TossPaymentResponse;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.repository.UserBalanceRepository;


@Component
public class TossClient {
	
	private static final Logger log = LoggerFactory.getLogger(TossClient.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PaymentOrderRepository orderRepository;
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@Value("${payments.toss.secret-key}")
	private String secretKey;
	
	@Value("${payments.toss.api-base-url}")
	private String baseUrl;

	public TossPaymentResponse confirm(String paymentKey, String orderId, long amount) {
		if (secretKey == null || secretKey.isBlank()) {
			throw new IllegalStateException("Toss secret key is empty");
		}
		
		if (paymentKey == null || paymentKey.isBlank() || orderId == null || orderId.isBlank()) {
			throw new IllegalArgumentException("paymentKey/orderId must not be blank");
		}
		
		String url = baseUrl + "/v1/payments/confirm";

		log.info("Toss confirm request: orderId={}, amount={}", orderId, amount);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		
		String encodedAuth = Base64.getEncoder()
				.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
		headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
		
	    
	    Map<String, Object> body = Map.of(
	    		"paymentKey", paymentKey,
	    		"orderId", orderId,
	    		"amount", amount
	    );
	    
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TossPaymentResponse> res =
                    restTemplate.postForEntity(url, request, TossPaymentResponse.class);
            if(!res.getStatusCode().is2xxSuccessful()) {
            	String errMsg = "Toss confirm non-2xx: " + res.getStatusCode();
            	log.warn(errMsg);
            	throw new IllegalStateException(errMsg);
            }
            
            TossPaymentResponse resBody = res.getBody();
            if (resBody == null) {
            	throw new IllegalStateException("Toss confirm returned empty body");
            }
            
            log.info("Toss confirm success: orderId={}, paymentKey={}, totalAmount={}",
            		orderId, paymentKey, resBody.getTotalAmount());
            
            return res.getBody();
            
        } catch (HttpStatusCodeException e) {
            // Toss 에러 바디를 로깅/전달하기 용이
        	String bodyStr = e.getResponseBodyAsString();
        	int status = e.getRawStatusCode();
        	log.warn("Toss confirm failed: status={}, body={}", status, bodyStr);
            throw new IllegalStateException("toss confirm failed (" + status + "): " + bodyStr, e);
            
        } catch (ResourceAccessException e) {
        	log.error("Toss confirm network/timeout error: {}", e.getMessage());
        	throw new IllegalStateException("toss confirm network error: " + e.getMessage(), e);
        	
        } catch (Exception e) {
        	log.error("Toss confirm unexpected error", e);
        	throw new IllegalStateException("toss confirm unexpected error: " + e.getMessage(), e);
        }
	}
	
	public TossPaymentResponse getPaymentByOrderId(String orderId) {
		String url = baseUrl + "/v1/payments/orders/" + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(secretKey, "");
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Void> request = new HttpEntity<>(headers);
		
		try {
			ResponseEntity<TossPaymentResponse> response = 
					restTemplate.exchange(url, HttpMethod.GET, request, TossPaymentResponse.class);
			return response.getBody();
		} catch (Exception ex) {
			System.err.println("[TOSS 조회 실패] orderId=" + orderId + " / " + ex.getMessage());
			return null;
		}
		
	}
	
//	@Transactional
//	public RefundResultResponse refundCharge(Long userId, String orderUid, String reason) {
//	
//	}
}
