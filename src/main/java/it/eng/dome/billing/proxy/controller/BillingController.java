package it.eng.dome.billing.proxy.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.invoicing.dto.ApplyTaxesRequestDTO;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.JSON;
import it.eng.dome.tmforum.tmf678.v4.model.AppliedCustomerBillingRate;
import it.eng.dome.billing.proxy.service.BillingProxyService;


@RestController
@RequestMapping("/billing")
public class BillingController {
	private static final Logger logger = LoggerFactory.getLogger(BillingController.class);

	@Autowired
	protected BillingProxyService billing;
	
	 /**
	 * The POST /billing/previewPrice REST API is invoked to calculate the price preview (i.e., prices and taxes) of a ProcuctOrder (TMF622-v4).
	 * 
	 * @param orderJson The ProductOrder (TMF622-v4) as a Json string for which the prices and taxes must be calculated
	 * @return The ProductOrder as a Json string with prices and taxes
	 * @throws Throwable If an error occurs during the calculation of the product order's price preview
	 */ 

	@RequestMapping(value = "/previewPrice", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculatePricePreview(@RequestBody String orderJson) throws Throwable {
		logger.info("Received request to calculate price preview");
		
		String orderWithPrice = billing.billingPreviewPrice(orderJson); 
		
		logger.info("Calculate Invoicing (preview price) to apply Taxes");
		return billing.invoicingPreviewTaxes(orderWithPrice);
	}

	 /**
     * The POST /billing/bill REST API is invoked to calculate the bill of a Product (TMF637-v4) with taxes.
     * 
     * @param BillingRequestDTO The DTO contains information about the Product (TMF637-v4), the TimePeriod (TMF678-v4) and the list of ProductPrice (TMF637-v4) for which the bill must be calculated.
     * @return The list of AppliedCustomerBillingRate as a Json string with taxes
     * @throws Throwable If an error occurs during the calculation of the bill for the Product
     */
	@RequestMapping(value = "/bill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws Throwable {
		logger.info("Received request to calculate the bill");
		
		String json = getBillRequestDTOtoJson(billRequestDTO);
		
		// Gets the AppliedCustomerBillingRate list invoking the billing-engine
		String billsWithPrice = billing.bill(json);
		
		logger.info("Calculate Invoicing (bill) to apply Taxes");
		
		Product product=billRequestDTO.getProduct();
		String productJson=product.toJson();
		
		// Gets the AppliedCustomerBillingRate list with taxes invoking the invoicing-service
		//1) Get ApplyTaxesRequestDTO as a json string
        String appyTaxesRequestJsonStr=getApplyTaxesRequestDTOtoJson(billsWithPrice,productJson);	
        
        //2) Invoke the invoicing-service
		
		return billing.billApplyTaxes(appyTaxesRequestJsonStr);
	}
	

	/**
	 * The POST /billing/instantBill REST API is invoked to calculate the bill of a Product (TMF637-v4) with taxes considering as startdate now.\n
	 * The API identifies the groups of ProductPrices and TimePeriod that must be billed considering now as startdate.
	 * 
	 * @param product The Product for which the bill(s) must be calculated considering as startdate now 
	 * @return The list of AppliedCustomerBillingRate as a Json string with taxes
	 * @throws Throwable If an error occurs during the calculation of the bill for the Product
	 */
	@RequestMapping(value = "/instantBill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateBill(@RequestBody Product product) throws Throwable {
		
		try {
			Assert.state(product!=null, "Cannot calculate bill for empty Product!");
			
			logger.info("Received request for calculate a bill for product with id: "+product.getId()+" and date now");
			
			// Get current Date
			Date date=new Date();
			
			// Invoke method to calculate the bills for data now
			return calculateBillForDate(product, date);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// Java exception is converted into HTTP status code by the ControllerExceptionHandler
			throw new Exception(e); //throw (e.getCause() != null) ? e.getCause() : e;
		}
		
		
	}
	
	/**
	 * The POST /billing/billForDate REST API is invoked to calculate the bill of a Product (TMF637-v4) with taxes considering as startdate the date in input.\n
	 * The API identifies the groups of ProductPrices and TimePeriod that must be billed considering the specified date.
	 * 
	 * @param product The Product for which the bill(s) must be calculated considering the specified date
	 * @param date The Date that must be considered as startdate
	 * @return Throwable If an error occurs during the calculation of the bill for the Product
	 */
	@RequestMapping(value = "/billForDate", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateBillForDate(@RequestBody Product product, Date date) throws Throwable {
		try {
			Assert.state(product!=null, "Cannot calculate bill for empty Product!");
			Assert.state(date!=null, "Cannot calculate bill for a date with an empty date!");
			
			logger.info("Received request for calculate a bill for product with id: "+product.getId()+" and date: "+date.getTime());
				
			// Identify the groups of ProcutPrices and TimePeriod for which the bills must be calculated (i.e., created one or more BillingRequestDTO that will be put in input to the "billing/bill" API)
			List<BillingRequestDTO> productPricesAntTimePeriodGroups=getProductPricesAntTimePeriodGroups(product,date);
			
			Assert.state(!CollectionUtils.isEmpty(productPricesAntTimePeriodGroups), "Cannot calculate bills for a product without ProductPrices that must be billed in the specified date!");
			
			String totalAppliedCustomerBillingRateJsonList=new String();
			for(BillingRequestDTO dto: productPricesAntTimePeriodGroups) {
				String appliedCustomerBillingRateJsonList=calculateBill(dto);
				totalAppliedCustomerBillingRateJsonList.concat(appliedCustomerBillingRateJsonList);
			}

			return totalAppliedCustomerBillingRateJsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// Java exception is converted into HTTP status code by the ControllerExceptionHandler
			throw new Exception(e); //throw (e.getCause() != null) ? e.getCause() : e;
		}
	}

	private String getBillRequestDTOtoJson(BillingRequestDTO billRequestDTO) {
		// product
		String productJson = billRequestDTO.getProduct().toJson();
		
		// timePeriod
		String timePeriodJson = billRequestDTO.getTimePeriod().toJson();
		
		// productPriceListJson
		StringBuilder productPriceListJson = new StringBuilder("[");
		for (int i = 0; i < billRequestDTO.getProductPrice().size(); i++) {
            if (i > 0) {
            	productPriceListJson.append(", ");
            }
            productPriceListJson.append(billRequestDTO.getProductPrice().get(i).toJson());
        }
		productPriceListJson.append("]");
		
		return "{ \"product\": " + capitalizeStatus(productJson) + ", \"timePeriod\": "+ timePeriodJson + ", \"productPrice\": "+ productPriceListJson +"}";
	} 
	
	private String capitalizeStatus(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		String capitalize = json;
		 try {
			ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(json);
			 String status = jsonNode.get("status").asText();
			 jsonNode.put("status", status.toUpperCase());
			 return objectMapper.writeValueAsString(jsonNode);

		} catch (Exception e) {			
			return capitalize;
		}
	}
	
	/*
	 * TODO
	 * Method to get the ApplyTaxesRequestDTO as a json string
	 * 
	 * @param appliedCustomerBillingRateListJson The AppliedCustomerBillingRate list represented as a JSON string
	 * @param productJson The Product represented as a JSON string
	 * @return The ApplyTaxesRequestDTO  represented as a JSON string
	 */
	private String getApplyTaxesRequestDTOtoJson(String appliedCustomerBillingRateListJson, String productJson) {
		// TODO Auto-generated method stub
		return null;
	}
	 /*
	  * Utility method used to identify for a specific Product and Date the groups of ProductPrices and TimePeriod that must be considered to make a bill.
	  * For each identified group will be generated a BillingRequestDTO containing the list of ProcutPrice, the TimePeriod and the Product.
	  * The returned list of BillingRequestDTO will be used to invoke (for each BillingRequestDTO) the API /billing/bill to calculate the bill with taxes.
	  *  
	  * @param product The Product that must be billed
	  * @param date The date that must be considered as startdate to calculate the ProductPrices and TimePeriod groups
	  * @return A list of BillingRequestDTO 
	  */
	private List<BillingRequestDTO> getProductPricesAntTimePeriodGroups(Product product, Date date) {
		// TODO Auto-generated method stub
		return null;
	}
}
