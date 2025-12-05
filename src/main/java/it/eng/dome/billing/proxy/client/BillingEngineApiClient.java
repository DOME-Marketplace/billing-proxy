package it.eng.dome.billing.proxy.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.billing.proxy.utils.URLUtils;
import it.eng.dome.brokerage.billing.dto.BillingPreviewRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.dto.InstantBillingRequestDTO;
import it.eng.dome.brokerage.model.Invoice;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;
import jakarta.validation.constraints.NotNull;

/**
 * This class represents a Client service to invoke, using {@link RestTemplate}, the REST APIs provided by the BillingEngine (BE) component 
 * to manage the calculation of the price preview and of the bills.
 */
@Service
public class BillingEngineApiClient {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingEngineApiClient.class);
	
	private final String PREVIEW_PRICE_PATH = "/billing/previewPrice";
	private final String BILL_PATH = "/billing/bill";
	private final String INSTANT_BILL_PATH = "/billing/instantBill";
	
	@Autowired
	private RestClient restClient;
	
	private  final String billinEngineUrl; 
	
	/**
	 * Constructor initializing the baseUrl of the default DOME BillingEngine component
	 * 
	 * @param baseUrl the base URL of the DOME BillingEngine component
	 */
    public BillingEngineApiClient(@Value("${billing.billing_engine}") String baseUrl) {
    	this.billinEngineUrl = baseUrl;
    }
    
    /**
     * Invokes the BillingEngine (BE) component for the calculation of the price preview of a {@link ProductOrder}
     * 
     * @param billingPreviewRequestDTO A {@link BillingPreviewRequestDTO} to give in input to the REST API /billing/previewPrice of the BE
     * @param endpoint The endpoint of the BE. If null the default DOME BE endpoint will be considered
     * @return the {@link ProductOrder} with prices
     * @throws BillingProxyException if an error occurs during the invocation of the REST API /billing/previewPrice
     */
    public ProductOrder billingPreviewPrice(@NotNull BillingPreviewRequestDTO billingPreviewRequestDTO, String endpoint) throws BillingProxyException{ 
    	
    	String url;
    	if(endpoint!=null)
    		url= URLUtils.buildUrl(endpoint, PREVIEW_PRICE_PATH);
    	else
    		url =URLUtils.buildUrl(billinEngineUrl, PREVIEW_PRICE_PATH);
    	
		logger.info("Invocation of Billing Engine API: {}", url);
		
		ResponseEntity<ProductOrder> response = restClient.post()
	        .uri(url)
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(billingPreviewRequestDTO)
	        .retrieve()
	        .toEntity(ProductOrder.class);
			
		if (response != null && response.getBody() != null) {
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the Billing Engine API: " + url + " - Response body is null");
		}
    }
    
    /**
     * Invokes the BillingEngine (BE) component for the calculation of the bill for a {@link Product} in a billingPeriod (i.e., {@link TimePeriod})
     * 
     * @param billingRequestDTO A {@link BillingRequestDTO} to give in input to the REST API /billing/bill of the BE
     * @param endpoint The endpoint of the BE. If null the default DOME BE endpoint will be considered
     * @return A list of {@link Invoice} with the calculate bills
     * @throws BillingProxyException if an {@link Error} occurs during the invocation of the REST API /billing/bill
     */
    public List<Invoice> billingBill(@NotNull BillingRequestDTO billingRequestDTO, String endpoint) throws BillingProxyException{
    	
    	String url;
    	if(endpoint!=null)
    		url= URLUtils.buildUrl(endpoint, BILL_PATH);
    	else
    		url =URLUtils.buildUrl(billinEngineUrl, BILL_PATH);
    	
		logger.info("Invocation of Billing Engine API: {}", url);
		
		ResponseEntity<List<Invoice>> response = restClient.post()
		        .uri(url)
		        .contentType(MediaType.APPLICATION_JSON)
		        .body(billingRequestDTO)
		        .retrieve()
		        .toEntity(new ParameterizedTypeReference<List<Invoice>>() {});
			
		if (response != null && response.getBody() != null) {
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the Billing Engine API: " + url + " - Response body is null");
		}
    }
    
    /**
     * Invokes the BillingEngine (BE) component for the calculation of the instant bill for a {@link Product} for a specific date
     * 
     * @param instantBillingRequestDTO A {@link InstantBillingRequestDTO} to give in input to the REST API /billing/instantBill of the BE
     * @param endpoint The endpoint of the BE. If null the default DOME BE endpoint will be considered
     * @return A list of {@link Invoice} with the calculate bills
     * @throws BillingProxyException if an {@link Error} occurs during the invocation of the REST API /billing/instantBill
     */
    public List<Invoice> billingInstantBill(@NotNull InstantBillingRequestDTO instantBillingRequestDTO, String endpoint) throws BillingProxyException{
    	
    	String url;
    	if(endpoint!=null)
    		url= URLUtils.buildUrl(endpoint, INSTANT_BILL_PATH);
    	else
    		url =URLUtils.buildUrl(billinEngineUrl, INSTANT_BILL_PATH);
    	
		logger.info("Invocation of Billing Engine API: {}", url);
		
		ResponseEntity<List<Invoice>> response = restClient.post()
		        .uri(url)
		        .contentType(MediaType.APPLICATION_JSON)
		        .body(instantBillingRequestDTO)
		        .retrieve()
		        .toEntity(new ParameterizedTypeReference<List<Invoice>>() {});
			
		if (response != null && response.getBody() != null) {
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the Billing Engine API: " + url + " - Response body is null");
		}
    }


}
