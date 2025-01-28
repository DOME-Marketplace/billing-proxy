package it.eng.dome.billing.proxy.controller;

import java.time.OffsetDateTime;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
>>>>>>> billing-proxy-instantBill

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
<<<<<<< HEAD
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf637.v4.model.ProductOfferingPriceRef;
import it.eng.dome.tmforum.tmf637.v4.model.ProductPrice;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;
=======

>>>>>>> billing-proxy-instantBill
import it.eng.dome.billing.proxy.service.BillingProxyService;
import it.eng.dome.billing.proxy.service.BillingService;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.utils.BillingUtils;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPrice;
import it.eng.dome.tmforum.tmf637.v4.JSON;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf637.v4.model.ProductPrice;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;


@RestController
@RequestMapping("/billing")
public class BillingController {
	private static final Logger logger = LoggerFactory.getLogger(BillingController.class);
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

	@Autowired
	protected BillingProxyService billing;
	
	@Autowired
	private BillingService billingService;
	
	private final static String PREFIX_KEY = "period-";
	

	
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
<<<<<<< HEAD
	public String calculateBill(@RequestBody BillingRequestDTO billRequestDTO) throws Throwable {
		logger.info("Received billingRequestDTO to calculate the bill");
=======
	public String calculateBill(@RequestBody String billRequestDTO) throws Throwable {
		logger.info("Received request to calculate the bill");
	
		// Gets the AppliedCustomerBillingRate list invoking the billing-engine
		String billsWithPrice = billing.bill(billRequestDTO);		
		logger.debug("Billing payload with price:\n" + billsWithPrice);

>>>>>>> billing-proxy-instantBill
		
		// Gets the AppliedCustomerBillingRate list with taxes invoking the invoicing-service
		//1) Get ApplyTaxesRequestDTO as a json string
		logger.info("Get ApplyTaxesRequestDTO from bill and product");
		BillingRequestDTO brDTO = JSON.deserialize(toLowerCaseStatus(billRequestDTO, "/product"), BillingRequestDTO.class);
		
		Product product = brDTO.getProduct();
		String productJson = JSON.getGson().toJson(product);	
        String appyTaxesRequestJsonStr = getApplyTaxesRequestDTOtoJson(billsWithPrice, productJson);	
        
        //2) Invoke the invoicing-service
        logger.info("Calculate Invoicing (bill) to apply Taxes");        
		return billing.billApplyTaxes(appyTaxesRequestJsonStr);
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

	/**
	 * The POST /billing/instantBill REST API is invoked to calculate the bill of a Product (TMF637-v4) with taxes considering as startdate now.\n
	 * The API identifies the groups of ProductPrices and TimePeriod that must be billed considering now as startdate.
	 * 
	 * @param product The Product for which the bill(s) must be calculated considering as startdate now 
	 * @return The list of AppliedCustomerBillingRate as a Json string with taxes
	 * @throws Throwable If an error occurs during the calculation of the bill for the Product
	 */
	@RequestMapping(value = "/instantBill", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public String calculateInstantBill(@RequestBody String product) throws Throwable {
		
		try {
			Assert.state(product!=null, "Cannot calculate bill for empty Product!");
			
			logger.info("Received request for calculate a bill now - instant bill");
			
			// Get current Date
			OffsetDateTime now = OffsetDateTime.now();
			logger.info("Starting calculate instant bill at {}", now.format(formatter));
			
			// Invoke method to calculate the bills for data now
			return calculateBillNow(product);
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
	//@RequestMapping(value = "/billForDate", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	/*public String calculateBillForDate(@RequestBody Product product, OffsetDateTime date) throws Throwable {
		try {
			Assert.state(product!=null, "Cannot calculate bill for empty Product!");
			Assert.state(date!=null, "Cannot calculate bill for a date with an empty date!");
			
			logger.info("Received request for calculate a bill for date "+date.format(formatter));
				
			// Identify the groups of ProcutPrices and TimePeriod for which the bills must be calculated (i.e., created one or more BillingRequestDTO that will be put in input to the "billing/bill" API)
			List<BillingRequestDTO> productPricesAntTimePeriodGroups=getProductPricesAntTimePeriodGroups(product,date);
			
			Assert.state(!CollectionUtils.isEmpty(productPricesAntTimePeriodGroups), "Cannot calculate bills for a product without ProductPrices that must be billed in the specified date!");
			
			String totalAppliedCustomerBillingRateJsonList=new String();
			for(BillingRequestDTO dto: productPricesAntTimePeriodGroups) {
				String appliedCustomerBillingRateJsonList=calculateBill(dto.toJson());
				totalAppliedCustomerBillingRateJsonList.concat(appliedCustomerBillingRateJsonList);
			}

			return totalAppliedCustomerBillingRateJsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// Java exception is converted into HTTP status code by the ControllerExceptionHandler
			throw new Exception(e); //throw (e.getCause() != null) ? e.getCause() : e;
		}
	}*/
	
	private String calculateBillNow(String prod) throws Throwable {
		try {
			Assert.state(prod!=null, "Cannot calculate bill for empty Product!");
			
			logger.info("Received request for calculate a bill now");
				
			// Identify the groups of ProcutPrices and TimePeriod for which the bills must be calculated (i.e., created one or more BillingRequestDTO that will be put in input to the "billing/bill" API)
			Product product = JSON.deserialize(toLowerCaseStatus(prod, ""), Product.class);
			List<BillingRequestDTO> productPricesAntTimePeriodGroups = getProductPricesAntTimePeriodGroupsForNow(product);
			
			//Assert.state(!CollectionUtils.isEmpty(productPricesAntTimePeriodGroups), "Cannot calculate bills for a product without ProductPrices that must be billed in the specified date!");
			StringBuilder appliedCustomerBillingRateJsonList = new StringBuilder("[");
			int i=0;
			
			for(BillingRequestDTO dto: productPricesAntTimePeriodGroups) {
				String appliedCustomerBillingRateListTemp = calculateBill(dto.toJson());
				if (i > 0) {
					appliedCustomerBillingRateJsonList.append(", ");
				}
				i++;			
				appliedCustomerBillingRateJsonList.append(cleanup(appliedCustomerBillingRateListTemp));
			}
			appliedCustomerBillingRateJsonList.append("]");
			
			logger.info("AppliedCustomerBillingRateList: {}", appliedCustomerBillingRateJsonList.toString());

			return appliedCustomerBillingRateJsonList.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// Java exception is converted into HTTP status code by the ControllerExceptionHandler
			throw new Exception(e); //throw (e.getCause() != null) ? e.getCause() : e;
		}
	}
	
	//Remove [ and ] parenthesis from string (array)
	private String cleanup(String str) {
		ObjectMapper objectMapper = new ObjectMapper();
        
		try {
			List<Map<String, Object>> list = objectMapper.readValue(str, new TypeReference<List<Map<String, Object>>>() {});
	        String result = objectMapper.writeValueAsString(list).replaceAll("^\\[|\\]$", "");
	        return result;
		} catch (Exception e) {
			logger.error("Cannot remove parenthesis [ and ] {}", e.getMessage());
		} 
		return str;
	}


	/*
	 * Method to get the ApplyTaxesRequestDTO as a json string
	 * 
	 * @param appliedCustomerBillingRateListJson The AppliedCustomerBillingRate list represented as a JSON string
	 * @param productJson The Product represented as a JSON string
	 * @return The ApplyTaxesRequestDTO  represented as a JSON string
	 */
	private String getApplyTaxesRequestDTOtoJson(String appliedCustomerBillingRateListJson, String productJson) {
		return "{ \"product\": " + capitalizeStatus(productJson) + ", \"appliedCustomerBillingRate\": " + appliedCustomerBillingRateListJson + "}";
	}
	 /*
	  * Utility method used to identify for a specific Product and Date the groups of ProductPrices and TimePeriod that must be considered to make a bill now.
	  * For each identified group will be generated a BillingRequestDTO containing the list of ProcutPrice, the TimePeriod and the Product.
	  * The returned list of BillingRequestDTO will be used to invoke (for each BillingRequestDTO) the API /billing/bill to calculate now the bill with taxes.
	  *  
	  * @param product The Product that must be billed
	  * @return A list of BillingRequestDTO 
	  */
	private List<BillingRequestDTO> getProductPricesAntTimePeriodGroupsForNow(Product product) throws Throwable{
		try {

			List<BillingRequestDTO> brDTOList=new ArrayList<BillingRequestDTO>();
			
			List<ProductPrice> pprices = product.getProductPrice();
			logger.debug("Number of ProductPrices found: {} ", pprices.size());

			Map<String, List<TimePeriod>> timePeriods = new HashMap<>();
			Map<String, List<ProductPrice>> productPrices = new HashMap<>();

			for (ProductPrice pprice : pprices) {

				//1) Get ProductOfferingPrice
				ProductOfferingPrice pop = billingService.getProductOfferingPrice(pprice.getProductOfferingPrice().getId());
				Assert.state(!Objects.isNull(pop), "The ProductOfferingPrice reference is missing in the ProductPrice " + pprice.getName());
				
				if("one-time".equals(pop.getPriceType().toLowerCase())) {
					logger.info("Calculate ProductPrice list and TimePeriod group for one-time");
					String keyPeriod = PREFIX_KEY + "one-time";
					
					// Get TimePeriod and ProductPrice for billing
					TimePeriod tp = new TimePeriod();
					tp.setStartDateTime(OffsetDateTime.now());
					tp.setEndDateTime(OffsetDateTime.now());
					
					// grouped items with the same startDate and endDate (i.e. keyPeriod)
					timePeriods.put(keyPeriod, new ArrayList<>(Arrays.asList(tp)));
					productPrices.computeIfAbsent(keyPeriod, k -> new ArrayList<>()).add(pprice);
					
				}else if("recurring-prepaid".equals(pop.getPriceType().toLowerCase())) {
					logger.info("Calculate ProductPrice list and TimePeriod group for recurring-prepaid");
					String keyPeriod = PREFIX_KEY + "recurring-prepaid";
					
					//Get recurringPeriod
					String recurringPeriod=pop.getRecurringChargePeriodLength()+" "+pop.getRecurringChargePeriodType();
					
					// Get TimePeriod and ProductPrice for billing
					TimePeriod tp = new TimePeriod();
					OffsetDateTime nextBillingTime = BillingUtils.getNextBillingTime(OffsetDateTime.now(), OffsetDateTime.now(), recurringPeriod);
					tp.setStartDateTime(OffsetDateTime.now());
					tp.setEndDateTime(nextBillingTime);
					
					// grouped items with the same startDate and endDate (i.e. keyPeriod)
					timePeriods.put(keyPeriod, new ArrayList<>(Arrays.asList(tp)));
					productPrices.computeIfAbsent(keyPeriod, k -> new ArrayList<>()).add(pprice);
				}else {
					logger.info("No calculate ProductPrice list and TimePeriod group for {}",pop.getPriceType());
				}
			}
			
			logger.info("Number of item for billing found: {}", productPrices.size());
			for (Map.Entry<String, List<ProductPrice>> entry : productPrices.entrySet()) {

				String key = entry.getKey();
				TimePeriod tp = timePeriods.get(key).get(0);

				if (!timePeriods.get(key).isEmpty()) {
					logger.debug("TimePeriod - startDate: " + tp.getStartDateTime() + " - endDate: " + tp.getEndDateTime());
					List<ProductPrice> pps = entry.getValue();
					
					//Create the BillingRequestDTO for the group
					BillingRequestDTO brDTO = new BillingRequestDTO(product, tp, pps);
					logger.info("BillingRequestDTO: {}", brDTO.toJson());
					// Add the BillingRequestDTO to the lists of BillingRequestDTO
					brDTOList.add(brDTO);
				}
			}
			
			return brDTOList;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	
	// Bugfix: ProductStatusType must be uppercase
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
	
	//TODO workaround to set the status value in lowercase
	private String toLowerCaseStatus(String json, String path) {
		ObjectMapper objectMapper = new ObjectMapper();
		String lower = json;
		try {
			ObjectNode rootNode = (ObjectNode) objectMapper.readTree(json);
			JsonNode statusNode = rootNode.at(path + "/status");
			if (!statusNode.isMissingNode()) {
				String status = statusNode.asText();
				((ObjectNode) rootNode.at(path)).put("status", status.toLowerCase());
			}
			return objectMapper.writeValueAsString(rootNode);

		} catch (Exception e) {			
			return lower;
		}
	}
}
