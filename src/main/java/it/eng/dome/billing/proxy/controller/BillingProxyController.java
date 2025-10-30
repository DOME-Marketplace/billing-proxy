package it.eng.dome.billing.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.eng.dome.billing.proxy.client.BillingEngineApiClient;
import it.eng.dome.billing.proxy.client.InvoicingServiceApiClient;
import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.billing.proxy.exception.BillingProxyValidationException;
import it.eng.dome.billing.proxy.service.BillingProxyService;
import it.eng.dome.brokerage.billing.dto.BillingPreviewRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingResponseDTO;
import it.eng.dome.brokerage.invoicing.dto.ApplyTaxesRequestDTO;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf635.v4.model.Usage;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;


@RestController
@RequestMapping("/billing")
public class BillingProxyController {
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyController.class);

	@Autowired
	private BillingProxyService billingProxyService;
	
	@Autowired
	private BillingEngineApiClient billingEngineApiClient;
	
	@Autowired
	private InvoicingServiceApiClient invoicingServiceApiClient;
	

	
	 /**
	 * The POST /billing/previewPrice REST API is invoked to calculate the price preview (i.e., prices and taxes) of a {@link ProductOrder}.
	 * 
	 * @param billingPreviewRequestDTO A {@link BillingPreviewRequestDTO} containing information about the {@link ProductOrder} and, in case of pay per use scenario, the list of simulate {@link Usage} for which the price preview must be calculated.
	 * @return The ProductOrder with prices and taxes
	 */ 
	@PostMapping("/previewPrice")
	public ResponseEntity<ProductOrder> calculatePricePreview(@RequestBody BillingPreviewRequestDTO billingPreviewRequestDTO){
		logger.info("Received request to calculate price preview...");
		
		try {
			if(billingPreviewRequestDTO.getProductOrder()==null)
				throw new BillingProxyException("Error in the BillingPreviewRequestDTO: the ProductOrder is null");
			else {
				ProductOrder productOrder=billingEngineApiClient.billingPreviewPrice(billingPreviewRequestDTO);
				return ResponseEntity.ok(invoicingServiceApiClient.invoicingPreviewTaxes(productOrder));
			}
			
		}catch (BillingProxyException e){
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	
	/**
     * The POST /billing/bill REST API is invoked to calculate the bill of a {@link Product} with taxes.
     * 
     * @param billRequestDTO {@link BillingResponseDTO} containing information about the {@link Product} and the billingPeriod (i.e., {@link TimePeriod}) for which the bill must be calculated.
     * @return A {@link BillingResponseDTO} containing the bill with taxes
     */
	@PostMapping("/bill")
	public ResponseEntity<BillingResponseDTO> calculateBill(@RequestBody BillingRequestDTO billRequestDTO){
		logger.info("Received request to calculate the bill...");
		

		try {
			if(billRequestDTO.getProduct()==null)
				throw new BillingProxyException("Error in the BillingRequestDTO: the Product is null");
			
			if(billRequestDTO.getTimePeriod()==null)
				throw new BillingProxyException("Error in the BillingRequestDTO: the billingPeriod is null");
			
			BillingResponseDTO billsWithoutTaxes=billingEngineApiClient.billingBill(billRequestDTO);
			
			ApplyTaxesRequestDTO applyTaxesRequest=new ApplyTaxesRequestDTO(billRequestDTO.getProduct(),billsWithoutTaxes.getCustomerBill(),billsWithoutTaxes.getAcbr());
				
			return ResponseEntity.ok(invoicingServiceApiClient.invoicingApplyTaxes(applyTaxesRequest));

				
		}catch (BillingProxyException e){
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	/**
	 * The POST /billing/instantBill REST API is invoked to calculate the bill of a {@link Product} with taxes now (i.e., the considered billingPeriod is now).
	 * 
	 * @param product The Product for which the bill must be calculated now 
	 * @return A {@link BillingResponseDTO} with taxes
	 */
	@Deprecated
	@RequestMapping(value = "/instantBill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<BillingResponseDTO> calculateInstantBill(@RequestBody Product product) {
		
		try {
			if(product==null)
				throw new BillingProxyException("Error in the instantBill request: the Product is null"); 

			logger.info("Received request for calculate a bill now - instant bill");
			
			// Creates a BillingRequestDTO with billingPeriod now
			BillingRequestDTO billingRequestDTO = billingProxyService.createBillingRequestDTOForInstantBill(product);
			
			if(billingRequestDTO==null) {
				throw new BillingProxyException("Error during the creation of the BillingRequestDTO!");
			}
			
			// Invoke method to calculate the bills for billingPeriod now
			ResponseEntity<BillingResponseDTO> billingResponse=this.calculateBill(billingRequestDTO);
			
			return ResponseEntity.ok(billingResponse.getBody());
		} catch (BillingProxyValidationException e) {
			logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}

}