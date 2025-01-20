package it.eng.dome.billing.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class BillingProxyService implements IProxyService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
	
	RestTemplate restTemplate = new RestTemplate();
	

	@Value("${billing.billing_engine}")
	public String billinEngine;


	public ResponseEntity<String> previewPrice(String order) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(order, headers);
		
		logger.debug("Payload received:\n" + order);
		ResponseEntity<String> response = restTemplate.postForEntity(billinEngine + "/billing/previewPrice", request, String.class);
		
		logger.debug("Headers: " + response.getHeaders().toString());
		logger.debug("Body:\n" + response.getBody().toString());
		
		return response;
	}

	public ResponseEntity<String> bill(String billRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(billRequest, headers);
		//logger.debug("Payload received:\n" + billRequest);
		return restTemplate.postForEntity(billinEngine + "/billing/bill", request, String.class);
	}

}
