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

	@Override
	public String billingPreviewPrice(String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String invoicingPreviewTaxes(String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bill(String bill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String billApplyTaxes(String bill) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//RestTemplate restTemplate = new RestTemplate();
	

	/*
	 * @Value("${billing.billing_engine}") public String billinEngine;
	 * 
	 * @Value("${billing.invoicing_service}") public String invoicingService;
	 */
	


	/*public String billingPreviewPrice(String billingPreviewRequestDTO) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(billingPreviewRequestDTO, headers);
		
		logger.debug("Payload billing preview price received:\n" + billingPreviewRequestDTO);
		ResponseEntity<String> response = restTemplate.postForEntity(billinEngine + "/billing/previewPrice", request, String.class);
			
		if (response != null && response.getBody() != null) {
			logger.debug("Headers: " + response.getHeaders().toString());
			logger.debug("Body:\n" + response.getBody().toString());
			return response.getBody().toString();
		}else {
			logger.warn("Response: ", (response == null) ? response : response.getBody());
			logger.debug("Cannot retrieve the billing preview price from {}", billinEngine);
			return null;
		}
	}
	
	public String invoicingPreviewTaxes(String order) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(order, headers);

		logger.debug("Payload invoicing preview taxes received:\n" + order);
		ResponseEntity<String> response = restTemplate.postForEntity(invoicingService + "/invoicing/previewTaxes", request, String.class);
		
		if (response != null && response.getBody() != null) {
			logger.debug("Headers: " + response.getHeaders().toString());
			logger.debug("Body:\n" + response.getBody().toString());
			return response.getBody().toString();
		
		}else {
			logger.warn("Response: ", (response == null) ? response : response.getBody());
			logger.debug("Cannot retrieve the invoicing preview price from {}", invoicingService);
			return null;
		}
	}
	

	public String bill(String bill) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(bill , headers);
		logger.debug("Payload bill received:\n" + bill);
		ResponseEntity<String> response = restTemplate.postForEntity(billinEngine + "/billing/bill", request, String.class);
		
		if (response != null && response.getBody() != null) {
			logger.debug("Headers: " + response.getHeaders().toString());
			logger.debug("Body:\n" + response.getBody().toString());
			return response.getBody().toString();
		
		}else {
			logger.warn("Response: ", (response == null) ? response : response.getBody());
			logger.debug("Cannot retrieve the bill from {}", billinEngine);
			return null;
		}
	}
	
	public String billApplyTaxes(String bill) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(bill , headers);
		logger.debug("Payload bill apply taxes received:\n" + bill);
		ResponseEntity<String> response = restTemplate.postForEntity(invoicingService + "/invoicing/applyTaxes", request, String.class);
		
		if (response != null && response.getBody() != null) {
			logger.debug("Headers: " + response.getHeaders().toString());
			logger.debug("Body:\n" + response.getBody().toString());
			return response.getBody().toString();
		
		}else {
			logger.warn("Response: ", (response == null) ? response : response.getBody());
			logger.debug("Cannot retrieve the bill apply taxes from {}", invoicingService);
			return null;
		}
	}*/

}
