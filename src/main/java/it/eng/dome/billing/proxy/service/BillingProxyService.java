package it.eng.dome.billing.proxy.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.eng.dome.billing.proxy.exception.BillingProxyValidationException;
import it.eng.dome.billing.proxy.validator.TMFEntityValidator;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;
import jakarta.validation.constraints.NotNull;


@Service
public class BillingProxyService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
	
	@Autowired
	private TMFEntityValidator tmfEntityValidator;
	
	/*@Override
	public void afterPropertiesSet() throws Exception {
		productCatalogManagementApis = new ProductCatalogManagementApis(tmfApiFactory.getTMF620CatalogApiClient());
	}*/

	
	
	public BillingRequestDTO createBillingRequestDTOForInstantBill(@NotNull Product product) throws BillingProxyValidationException {

		OffsetDateTime now = OffsetDateTime.now();
		logger.info("Creating BillingRequestDTO for Product {} and dateTime {}", product, now.format(formatter));
		
		// Validate Product
		tmfEntityValidator.validateProduct(product);
		
		TimePeriod billingPeriod=new TimePeriod();
		billingPeriod.setStartDateTime(now);
		billingPeriod.setEndDateTime(now);
		
		return new BillingRequestDTO(product, billingPeriod, null);
		
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
