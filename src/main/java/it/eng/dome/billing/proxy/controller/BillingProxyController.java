package it.eng.dome.billing.proxy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.billing.proxy.service.BillingProxyService;
import it.eng.dome.brokerage.billing.dto.BillingPreviewRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.dto.InstantBillingRequestDTO;
import it.eng.dome.brokerage.model.Invoice;

import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf637.v4.ApiException;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;


@RestController
@RequestMapping("/billing")
public class BillingProxyController {
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyController.class);
	
	@Autowired
	private BillingProxyService billingProxyService;
	//private BillingEngineApiClient billingEngineApiClient;
	
	@Autowired
	//private InvoicingServiceApiClient invoicingServiceApiClient;
	
	 /**
	 * The REST API POST /billing/previewPrice is invoked to calculate the price preview (i.e., prices and taxes) of a {@link ProductOrder}.
	 * 
	 * @param billingPreviewRequestDTO A {@link BillingPreviewRequestDTO} containing information about the {@link ProductOrder} and, in case of pay-per-use scenario, the list of simulate {@link Usage} for which the price preview must be calculated.
	 * @return The ProductOrder with prices and taxes
	 * @throws BillingProxyException if an error occurs during the processing
	 */ 
	@PostMapping("/previewPrice")
	public ResponseEntity<ProductOrder> calculatePricePreview(@RequestBody BillingPreviewRequestDTO billingPreviewRequestDTO) throws BillingProxyException{
		logger.info("Received request to calculate price preview...");
		
		if(billingPreviewRequestDTO.getProductOrder()==null)
			throw new BillingProxyException("Error in the BillingPreviewRequestDTO: the ProductOrder is null");
		else {
			ProductOrder productOrder=billingProxyService.billingPreviewPrice(billingPreviewRequestDTO);
			return ResponseEntity.ok(billingProxyService.invoicingPreviewTaxes(productOrder));
		}
	}

	
	/**
     * The POST /billing/bill REST API is invoked to calculate the bill of a {@link Product} with taxes.
     * 
     * @param billRequestDTO A {@link BillingRequestDTO} with information about the Product's identifier and the billingPeriod (i.e., {@link TimePeriod}) for which the bill must be calculated.
     * @return A list of {@link Invoice} with taxes
	 * @throws BillingProxyException if an error occurs during the processing
	 * @throws it.eng.dome.tmforum.tmf637.v4.ApiException 
     */
	@PostMapping("/bill")
	public ResponseEntity<List<Invoice>> calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws BillingProxyException, ApiException{
		logger.info("Received request to calculate the bill...");
		

		if(billRequestDTO.getProductId()==null || billRequestDTO.getProductId().isEmpty())
			throw new BillingProxyException("Error in the BillingRequestDTO: the identifier of the Product is null");
		
		if(billRequestDTO.getBillingPeriod()==null)
			throw new BillingProxyException("Error in the BillingRequestDTO: the billingPeriod is null");
		
		List<Invoice> billsWithoutTaxes=billingProxyService.billingBill(billRequestDTO);
			
		return ResponseEntity.ok(billingProxyService.invoicingApplyTaxes(billsWithoutTaxes));

	}


	/**
	 * The REST API POST /billing/instantBill is invoked to calculate the bill of a {@link Product}, not yet in the Product Inventory, with taxes for a specific date.
	 * 
	 * @param InstantBillingRequestDTO An {@link InstantBillingRequestDTO}  
	 * @return A list of {@link Invoice} with taxes
	 * @throws BillingProxyException if an error occurs during the processing
	 */
	@RequestMapping(value = "/instantBill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<List<Invoice>> calculateInstantBill(@RequestBody InstantBillingRequestDTO instantBillingRequestDTO) throws BillingProxyException {
		

		if(instantBillingRequestDTO.getProduct()==null)
			throw new BillingProxyException("Error in the InstantBillingRequestDTO: the Product is null"); 
		
		if(instantBillingRequestDTO.getDate()==null)
			throw new BillingProxyException("Error in the InstantBillingRequestDTO: the date is null"); 

		logger.info("Received request for calculate an instant bill for date {}",instantBillingRequestDTO.getDate());
		
		
		List<Invoice> billsWithoutTaxes=billingProxyService.billingInstantBill(instantBillingRequestDTO);			

		return ResponseEntity.ok(billingProxyService.invoicingApplyTaxes(billsWithoutTaxes));

	}

}