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
import it.eng.dome.brokerage.model.Invoice;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import jakarta.validation.constraints.NotNull;

/**
 * This class represents a Client service to invoke,  using {@link RestTemplate}, the REST APIs provided by the InvoicingService (IS) component 
 * to manage the calculation of the price preview with taxes and the bills with taxes.
 */
@Service
public class InvoicingServiceApiClient {
	private static final Logger logger = LoggerFactory.getLogger(InvoicingServiceApiClient.class);
	
	private final String PREVIEW_TAXES_PATH = "/invoicing/previewTaxes";
	private final String APPLY_TAXES_PATH = "/invoicing/applyTaxes";
	
	@Autowired
	private RestClient restClient;
	private final String invoiceServiceUrl;
	
	/**
	 * Constructor initializing the baseUrl of the default DOME InvoicingService component
	 * 
	 * @param baseUrl the base URL of the DOME InvoicingService component
	 */
    public InvoicingServiceApiClient(@Value("${billing.invoicing_service}") String baseUrl) {
    	this.invoiceServiceUrl = baseUrl;
    }
    
    /**
     * Invokes the InvoicingService (IS) component for the calculation of the price preview with taxes of a {@link ProductOrder}
     * 
     * @param productOrder the ProductOrder to which the taxes must be applied
     * @param endpoint The endpoint of the IS. If null the default DOME IS endpoint will be considered
     * @return the ProductOrder with price and taxes
     * @throws BillingProxyException if an error occurs during the invocation of the /invoicing/previewTaxes REST API
     */
    public ProductOrder invoicingPreviewTaxes(@NotNull ProductOrder productOrder, String endpoint) throws BillingProxyException{
    	
    	String url;
    	if(endpoint!=null)
    		url= URLUtils.buildUrl(endpoint, PREVIEW_TAXES_PATH);
    	else
    		url =URLUtils.buildUrl(invoiceServiceUrl, PREVIEW_TAXES_PATH);

		logger.info("Invocation of InvoicingService API: {}", PREVIEW_TAXES_PATH);
		
		ResponseEntity<ProductOrder> response = restClient.post()
	        .uri(url)
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(productOrder)
	        .retrieve()
	        .toEntity(ProductOrder.class);
		
			
		if (response != null && response.getBody() != null) {
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: " + url + " - Response body is null"); 
		}
    }
    
    /**
     * Invokes the InvoicingService (IS) component to apply taxes to a list of {@link Invoice}
     * 
     * @param invoices A list of {@link Invoice} to give in input to the REST API /invoicing/applyTaxes of the IS
     * @param endpoint The endpoint of the IS. If null the default DOME IE endpoint will be considered 
     * @return A list of {@link Invoice} with taxes
     * @throws BillingProxyException  if an error occurs during the invocation of the REST API /invoicing/applyTaxes 
     */
    public List<Invoice> invoicingApplyTaxes(@NotNull List<Invoice> invoices, String endpoint) throws BillingProxyException{
    	
    	String url;
    	if(endpoint!=null)
    		url= URLUtils.buildUrl(endpoint, APPLY_TAXES_PATH);
    	else
    		url =URLUtils.buildUrl(invoiceServiceUrl, APPLY_TAXES_PATH);
    	
		logger.info("Invocation of InvoicingService API: {}", APPLY_TAXES_PATH);

		ResponseEntity<List<Invoice>> response = restClient.post()
	        .uri(url)
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(invoices)
	        .retrieve()
	        .toEntity(new ParameterizedTypeReference<List<Invoice>>() {});
			
		if (response != null && response.getBody() != null) {
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: " + url + " - Response body is null"); 
		}
    }
}
