package com.example.newssummary.service.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.net.HttpHeaders;

import reactor.core.publisher.Mono;

@Component
public class TossClient {
	
	@Autowired
	private WebClient webClient = WebClient.builder().build();
	
	@Value("${payments.toss.secret-key}")
	private String secretKey;
	
	@Value("${payments.toss.api-base-url}")
	private String apiBase;
	
	public Mono<TossConfirmResponse> confirm(String paymentKey, String orderId, long amount) {
		String basicAuth = Base64.getEncoder()
				.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
		
		return webClient.post()
				.uri(apiBase + "/v1/paymnets/confirm")
				.header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new TossConfirmRequest(paymentKey, orderId, amount))
				.retrieve()
				.bodyToMono(TossConfirmResponse.class);
	}
	
	public record TossConfirmRequest(String paymentKye, String orderId, long amount) {}
	
	public record TossConfirmResponse(String orderId, String status, String method, Long totalAmount) {}
}
