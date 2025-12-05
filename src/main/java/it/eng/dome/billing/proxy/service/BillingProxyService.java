package it.eng.dome.billing.proxy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dome.billing.proxy.client.BillingEngineApiClient;
import it.eng.dome.billing.proxy.client.InvoicingServiceApiClient;
import it.eng.dome.billing.proxy.exception.BillingProxyException;
import it.eng.dome.brokerage.api.ProductCatalogManagementApis;
import it.eng.dome.brokerage.api.ProductInventoryApis;
import it.eng.dome.brokerage.billing.dto.BillingPreviewRequestDTO;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.brokerage.billing.dto.InstantBillingRequestDTO;
import it.eng.dome.brokerage.model.Invoice;
import it.eng.dome.tmforum.tmf620.v4.ApiException;
import it.eng.dome.tmforum.tmf620.v4.model.PricingLogicAlgorithm;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOffering;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOfferingRef;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrderItem;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import jakarta.validation.constraints.NotNull;

public class BillingProxyService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
	
	@Autowired
	private ProductCatalogManagementApis productCatalogManagementApis;
	
	@Autowired
	private ProductInventoryApis productInventoryApis;
	
	@Autowired
	private BillingEngineApiClient billingEngineApiClient;
	
	@Autowired
	private InvoicingServiceApiClient invoicingServiceApiClient;
	
	@Autowired
	private ProductOrderService productOrderService;
	
	
	public ProductOrder billingPreviewPrice(@NotNull BillingPreviewRequestDTO billingPreviewRequestDTO) throws BillingProxyException{
		
		ProductOrder updatedProductOrder=null;
		ProductOrder productOrderToUpdate=billingPreviewRequestDTO.getProductOrder();
		
		if(productOrderToUpdate==null)
			throw new BillingProxyException("Error in the BillingPreviewRequestDTO: the ProductOrder is null");

		List<ProductOrderItem> productOrderItems=productOrderToUpdate.getProductOrderItem();
		if(productOrderItems==null || productOrderItems.isEmpty())
			throw new BillingProxyException("Error: the list of ProductOrderItem in the ProductOrder "+productOrderToUpdate.getId());
		
		// Group ProductOrderItem(s) by URL, where "__NO_URL__" represents URLs that are not present (default DOME BE will be used)
		Map<String, List<ProductOrderItem>> mapByUrl =
				productOrderItems.stream()
		             .collect(Collectors.groupingBy(
		                 item -> {
		                	 ProductOfferingRef productOfferingRef=item.getProductOffering();
		                	 
		                	 String url = (productOfferingRef == null)
		                             ? null
		                             : this.getPricingLogicAlgorithm(productOfferingRef.getId());
		                	 
		                     //String url = this.getPricingLogicAlgorithm(item.getProductOffering());
		                     return (url == null || url.isBlank()) ? "__NO_URL__" : url;
		                 }
		             ));
		
		// USE CASE 1: All without URLs. Will be invoked the DOME BE for all the ProductOrderItem(s)
		if (mapByUrl.size() == 1 && mapByUrl.containsKey("__NO_URL__")) {
			updatedProductOrder=billingEngineApiClient.billingPreviewPrice(billingPreviewRequestDTO, null);
			
		} 
		// USE CASE 2: All with URLs and all the same. Will be invoked the external BE for all the ProductOrderItem(s)
		else if (mapByUrl.size() == 1 && !mapByUrl.containsKey("__NO_URL__")) {
		    String url = mapByUrl.keySet().iterator().next();
		    updatedProductOrder=billingEngineApiClient.billingPreviewPrice(billingPreviewRequestDTO, url);
		    
		}else {
		// USE CASE 3: Mixed case: some with URLs, some without. Will be invoked the BE (external BE or the DOME BE) for each ProductOrderItem
			
			// List to maintain the temporary ProductOrder(s) generated for each ProductOrderItem 
			List<ProductOrder> temporaryProductOrders=new ArrayList<ProductOrder>();
			
			for (Map.Entry<String, List<ProductOrderItem>> entry : mapByUrl.entrySet()) {

			    String url = entry.getKey();
			    List<ProductOrderItem> items = entry.getValue();

			   // if ("__NO_URL__".equals(url)) {

			        for (ProductOrderItem item : items) {
			            ProductOrder tempProdutOrder=productOrderService.uptateProductOrderItems(productOrderToUpdate,
			            		new ArrayList<ProductOrderItem>(List.of(item)));
			            
			            BillingPreviewRequestDTO tempBillingPreviewRequestDTO=new BillingPreviewRequestDTO(tempProdutOrder, billingPreviewRequestDTO.getUsage());
			            
			            tempProdutOrder=billingEngineApiClient.billingPreviewPrice(tempBillingPreviewRequestDTO, url);
			            temporaryProductOrders.add(tempProdutOrder);
			      //  }
			  //  } else {
			        // servizio Y per ciascun gruppo con URL
			  //      for (ProductOrderItem item : items) {
			   //         callServiceY(url, item);
			   //    }
			    }
			}
			
			List<ProductOrderItem> updatedProductOrderItems=productOrderService.retrieveProductOrderItems(temporaryProductOrders);
			updatedProductOrder=productOrderService.rebuildProductOrder(productOrderToUpdate, updatedProductOrderItems);
		}
		
		return updatedProductOrder;
	}


	private String getPricingLogicAlgorithm(@NotNull String productOfferingId){
		
		if(productOfferingId!=null && !productOfferingId.isBlank()) {
		
			String fields = "pricingLogicAlgorithm";
		
			ProductOffering productOffering;
			try {
				productOffering = productCatalogManagementApis.getProductOffering(productOfferingId, fields);
			} catch (ApiException e) {
				logger.error(e.getMessage());
				return null;
			}
		
			List<PricingLogicAlgorithm> pricingLogicAlgoritms= productOffering.getPricingLogicAlgorithm();
		
			if(pricingLogicAlgoritms!=null && !pricingLogicAlgoritms.isEmpty()) {
				PricingLogicAlgorithm pricingLogicAlgorithm=pricingLogicAlgoritms.get(0);
				String beUrl=pricingLogicAlgorithm.getPlaSpecId();
				if(beUrl!=null && !beUrl.isBlank()) {
					logger.debug("Retrieved pricingLogicAlgorithm with URL {} in ProductOffering {}", beUrl,productOfferingId);
					return beUrl;
				}
			}
		}
		
		return null;
	}
	
	public ProductOrder invoicingPreviewTaxes(@NotNull ProductOrder productOrder) throws BillingProxyException{
		return invoicingServiceApiClient.invoicingPreviewTaxes(productOrder, null);
	}
	
	public List<Invoice> billingBill(@NotNull BillingRequestDTO billingRequestDTO) throws BillingProxyException, it.eng.dome.tmforum.tmf637.v4.ApiException{
		List<Invoice> invoices=new ArrayList<Invoice>();
		
		if(billingRequestDTO.getProductId()==null || billingRequestDTO.getProductId().isBlank())
			throw new BillingProxyException("Missing the identifier of the Product in the BillingRequestDTO");
		
		String fields="productOffering";
		Product product=productInventoryApis.getProduct(billingRequestDTO.getProductId(), fields);
		
		String beEnpoint=null;
		if(product.getProductOffering()!=null) {
			beEnpoint=this.getPricingLogicAlgorithm(product.getProductOffering().getId());
		}
		
		invoices=billingEngineApiClient.billingBill(billingRequestDTO, beEnpoint);	
		
		return invoices;
	}
	
	public List<Invoice> invoicingApplyTaxes(@NotNull List<Invoice> invoices) throws BillingProxyException{
		return invoicingServiceApiClient.invoicingApplyTaxes(invoices, null);
	}
	
	public List<Invoice> billingInstantBill(@NotNull InstantBillingRequestDTO instantBillingRequestDTO) throws BillingProxyException{
		List<Invoice> invoices=new ArrayList<Invoice>();
		
		Product product=instantBillingRequestDTO.getProduct();
		
		if(product==null)
			throw new BillingProxyException("Missing the Product in the InstantBillingRequestDTO");
		
		String beEnpoint=null;
		
		if(product.getProductOffering()!=null) {
			beEnpoint=this.getPricingLogicAlgorithm(product.getProductOffering().getId());
		}
		
		invoices=billingEngineApiClient.billingInstantBill(instantBillingRequestDTO, beEnpoint);
		
		return invoices;
	}
	

}
