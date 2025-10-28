package it.eng.dome.billing.proxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.brokerage.billing.dto.BillingResponseDTO;
import it.eng.dome.brokerage.invoicing.dto.ApplyTaxesRequestDTO;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
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
     * @param productOrder the ProductOrder to which the taxes must be applied
     * @return the ProductOrder with price and taxes
     * @throws BillingProxyException if an error occurs during the invocation of the /invoicing/previewTaxes REST API
     */
    public ProductOrder invoicingPreviewTaxes(@NotNull ProductOrder productOrder) throws BillingProxyException{
    	
    	String url = invoiceServiceUrl + PREVIEW_TAXES_PATH;
		logger.info("Invocation of InvoicingService API: {}", PREVIEW_TAXES_PATH);
		
		ResponseEntity<ProductOrder> response = restClient.post()
	        .uri(url)
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(productOrder)
	        .retrieve()
	        .toEntity(ProductOrder.class);
		
			
		if (response != null && response.getBody() != null) {
			//logger.debug("Responce Body:\n" + response.getBody().toJson());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: " + url + " - Response body is null"); 
		}
    }
    
    /**
     * Invokes the InvoicingService (IS) component to apply taxes to the bills of a {@link Product}
     * @param applyTaxesRequestDTO A {@link ApplyTaxesRequestDTO} to give in input to the /invoicing/applyTaxes REST API of the IS
     * @return The {@link BillingResponseDTO} with taxes
     * @throws BillingProxyException  if an error occurs during the invocation of the /invoicing/applyTaxes REST API
     */
    public BillingResponseDTO invoicingApplyTaxes(@NotNull ApplyTaxesRequestDTO applyTaxesRequestDTO) throws BillingProxyException{
    	
    	String url = invoiceServiceUrl + APPLY_TAXES_PATH;
		logger.info("Invocation of InvoicingService API: {}", APPLY_TAXES_PATH);

		ResponseEntity<BillingResponseDTO> response = restClient.post()
	        .uri(url)
	        .contentType(MediaType.APPLICATION_JSON)
	        .body(applyTaxesRequestDTO)
	        .retrieve()
	        .toEntity(BillingResponseDTO.class);
			
		if (response != null && response.getBody() != null) {
			//logger.debug("Responce Body:\n" + response.getBody().toString());
			return response.getBody();
		}else {
			throw new BillingProxyException("Error in the invocation of the InvoicingService API: " + url + " - Response body is null"); 
		}
    }
}
