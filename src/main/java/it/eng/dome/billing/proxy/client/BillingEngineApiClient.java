package it.eng.dome.billing.proxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.brokerage.billing.dto.BillingPreviewRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingResponseDTO;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import jakarta.validation.constraints.NotNull;

@Service
public class BillingEngineApiClient {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingEngineApiClient.class);

	@Autowired
	private RestTemplate restTemplate;
	private  final String billinEngineUrl; 
	
	
    public BillingEngineApiClient( @Value("${billing.billing_engine}") String baseUrl) {
    	this.billinEngineUrl = baseUrl;
    }
    
    public ProductOrder billingPreviewPrice(@NotNull BillingPreviewRequestDTO billingPreviewRequestDTO) throws BillingProxyException{ 
    	
    	String url = billinEngineUrl + "/billing/previewPrice";
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<BillingPreviewRequestDTO> request = new HttpEntity<>(billingPreviewRequestDTO, headers);
		
		logger.info("Invocation of BillingEngine API: /billing/previewPrice...");
		ResponseEntity<ProductOrder> response = restTemplate.postForEntity(url, request, ProductOrder.class);
			
		if (response != null && response.getBody() != null) {
			logger.debug("Responce Body:\n" + response.getBody().toJson());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the BillingEngine API: /billing/previewPrice - Response body is null"); 
		}
    }
    
    public BillingResponseDTO billingBill(@NotNull BillingRequestDTO billingRequestDTO) throws BillingProxyException{
    	
    	String url = billinEngineUrl + "/billing/bill";
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<BillingRequestDTO> request = new HttpEntity<>(billingRequestDTO, headers);
		
		logger.info("Invocation of BillingEngine API: /billing/bill...");
		ResponseEntity<BillingResponseDTO> response = restTemplate.postForEntity(url, request, BillingResponseDTO.class);
			
		if (response != null && response.getBody() != null) {
			logger.debug("Responce Body:\n" + response.getBody().toString());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the BillingEngine API: /billing/bill - Response body is null"); 
		}
    }


}
