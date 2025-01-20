package it.eng.dome.billing.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.eng.dome.billing.proxy.service.BillingProxyService;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;


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

	@RequestMapping(value = "/pricePreview", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> calculatePricePreview(@RequestBody String orderJson) throws Throwable {
		logger.info("Received request to calculate price preview");
		return billing.pricePreview(orderJson);
	}

	 /**
     * The POST /billing/bill REST API is invoked to calculate the bill of a Product (TMF637-v4) without taxes.
     * 
     * @param BillingRequestDTO The DTO contains information about the Product (TMF637-v4), the TimePeriod (TMF678-v4) and the list of ProductPrice (TMF637-v4) for which the bill must be calculated.
     * @return The list of AppliedCustomerBillingRate as a Json without taxes
     * @throws Throwable If an error occurs during the calculation of the bill for the Product
     */
	
	@RequestMapping(value = "/bill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws Throwable {
		logger.info("Received request to calculate the bill");
		
		String json = getBillRequestDTOtoJson(billRequestDTO);
		logger.debug(json);
		
		return billing.bill(json);
	}
	
	 /**
     * The POST /billing/bill REST API is invoked to calculate the bill of a Product (TMF637-v4) without taxes.
     * 
     * @param BillingRequestDTO The DTO contains information about the Product (TMF637-v4), the TimePeriod (TMF678-v4) and the list of ProductPrice (TMF637-v4) for which the bill must be calculated.
     * @return The list of AppliedCustomerBillingRate as a Json without taxes
     * @throws Throwable If an error occurs during the calculation of the bill for the Product
     */
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
}
