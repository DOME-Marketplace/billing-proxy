package it.eng.dome.billing.proxy.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf637.v4.model.ProductOfferingPriceRef;
import it.eng.dome.tmforum.tmf637.v4.model.ProductPrice;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;
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
     * The POST /billing/bill REST API is invoked to calculate the bill of a Product (TMF637-v4) without taxes.
     * 
     * @param BillingRequestDTO The DTO contains information about the Product (TMF637-v4), the TimePeriod (TMF678-v4) and the list of ProductPrice (TMF637-v4) for which the bill must be calculated.
     * @return The list of AppliedCustomerBillingRate as a Json without taxes
     * @throws Throwable If an error occurs during the calculation of the bill for the Product
     */
	
	@RequestMapping(value = "/bill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws Throwable {
		logger.info("Received billingRequestDTO to calculate the bill");
		
		String json = getBillRequestDTOtoJson(billRequestDTO);
		
		String billWithPrice = billing.bill(json);
		
		logger.info("Calculate Invoicing (bill) to apply Taxes");
		return billing.billApplyTaxes(billWithPrice);
	}

	
	@RequestMapping(value = "/instantBill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateInstantBill(@RequestBody Product product) throws Throwable {
		logger.info("Received product request to calculate the instantBill");
		
		OffsetDateTime now = OffsetDateTime.now();
		TimePeriod tp = new TimePeriod().startDateTime(now).endDateTime(now);	
		String productId = product.getId();
		//TODO come recuperare il ProductPrice
		ArrayList<ProductPrice> productPriceList = new ArrayList<ProductPrice>();
		
		ProductPrice pp = new ProductPrice();
		ProductOfferingPriceRef popr = new ProductOfferingPriceRef();
		popr.setId("urn:ngsi-ld:product-offering-price:38b293a6-92db-4ca3-8fe6-54a6e4a9e12c");
		pp.setPriceType("recurring");
		pp.setProductOfferingPrice(popr);
		productPriceList.add(pp);

		BillingRequestDTO billRequestDTO = new BillingRequestDTO(product, tp, productPriceList);
		String json = getBillRequestDTOtoJson(billRequestDTO);
		String billWithPrice = billing.bill(json);
		
		logger.info("Calculate Invoicing (instantBill) to apply Taxes");
		return billing.billApplyTaxes(billWithPrice);
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
