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

	@RequestMapping(value = "/pricePreview", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> calculatePricePreview(@RequestBody String orderJson) throws Throwable {
		logger.info("Received request to calculate price preview");
		return billing.pricePreview(orderJson);
	}

	
	@RequestMapping(value = "/bill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws Throwable {
		logger.info("Received request to calculate the bill");
		
		String json = getBillRequestDTOtoJson(billRequestDTO);
		logger.debug(json);
		
		return billing.bill(json);
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
}
