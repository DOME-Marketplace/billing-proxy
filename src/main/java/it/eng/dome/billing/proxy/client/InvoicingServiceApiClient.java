package it.eng.dome.billing.proxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.brokerage.invoicing.dto.ApplyTaxesRequestDTO;
import it.eng.dome.brokerage.invoicing.dto.ApplyTaxesResponseDTO;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import jakarta.validation.constraints.NotNull;

@Service
public class InvoicingServiceApiClient {
	private static final Logger logger = LoggerFactory.getLogger(InvoicingServiceApiClient.class);
	
	private final RestTemplate restTemplate;
	private final String invoiceServiceUrl;
	
    public InvoicingServiceApiClient(RestTemplate restTemplate,
             @Value("${billing.invoicing_service}") String baseUrl) {
    	this.restTemplate = restTemplate;
    	this.invoiceServiceUrl = baseUrl;
    }
    
    public ProductOrder invoicingPreviewTaxes(@NotNull ProductOrder productOrder) throws BillingProxyException{
    	
    	String url = invoiceServiceUrl + "/invoicing/previewTaxes";
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ProductOrder> request = new HttpEntity<>(productOrder, headers);
		
		logger.info("Invocation of InvoicingService API: /invoicing/previewTaxes...");
		ResponseEntity<ProductOrder> response = restTemplate.postForEntity(url, request, ProductOrder.class);
			
		if (response != null && response.getBody() != null) {
			logger.debug("Responce Body:\n" + response.getBody().toString());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: /invoicing/previewTaxes - Response body is null"); 
		}
    }
    
    public ApplyTaxesResponseDTO invoicingApplyTaxes(@NotNull ApplyTaxesRequestDTO applyTaxesRequestDTO) throws BillingProxyException{
    	
    	String url = invoiceServiceUrl + "/invoicing/applyTaxes";
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ApplyTaxesRequestDTO> request = new HttpEntity<>(applyTaxesRequestDTO, headers);
		
		logger.info("Invocation of InvoicingService API: /invoicing/applyTaxes...");
		ResponseEntity<ApplyTaxesResponseDTO> response = restTemplate.postForEntity(url, request, ApplyTaxesResponseDTO.class);
			
		if (response != null && response.getBody() != null) {
			logger.debug("Responce Body:\n" + response.getBody().toString());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: /invoicing/applyTaxes - Response body is null"); 
		}
    }
}
