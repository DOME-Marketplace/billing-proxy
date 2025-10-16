package it.eng.dome.billing.proxy.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf635.v4.model.Usage;

/**
 * This class represents the DTO used by the "/billing/previewPrice" API to calculate the price preview of a ProcuctOrder (TMF622-v4).
 * This class contains information about the ProcuctOrder and, in case of pay-per-use, it also contains the list of usage data for which the price preview must be calculated.
 */
public class BillingPreviewRequestDTO{
 	
	private ProductOrder productOrder;
	private List<Usage> usage;
	
	/** 
	* Class constructor.
	*/
	public BillingPreviewRequestDTO(){}
	
	/**
	 * Class constructor specifying the {@link ProductOrder}, and the list of {@link Usage} for which the price preview will be calculated.
	 * The list of Usage is velarized only if the order refers to the pay-per-use use case.
	 * 
	 * @param productOrder the ProductOrder instance
	 * @param usageData the simulated Usage data for the ProductOrder in case of a pay-per-use offering  
	 */
	@JsonCreator
	public BillingPreviewRequestDTO(@JsonProperty("productOrder") ProductOrder productOrder, @JsonProperty("usage") List<Usage> usageData) {
		this.setProductOrder(productOrder);
		this.setUsage(usageData);
	}
	
	/**
	 * Returns the {@link ProductOrder} for which the price preview must be calculated
	 * 
	 * @return The ProductOrder for which the price preview must be calculated
	 */
	public ProductOrder getProductOrder() {
		return productOrder;
	}

	/**
	 * Sets the {@link ProductOrder} for which the price preview must be calculated
	 * 
	 * @param productOrder The ProductOrder for which the price preview must be calculated
	 */
	public void setProductOrder(ProductOrder productOrder) {
		this.productOrder = productOrder;
	}

	/**
	 * Returns the list of the simulated {@link Usage} of the {@link ProductOrder}
	 * 
	 * @return The list of the simulated Usage of the ProductOrder 
	 */
	public List<Usage> getUsage() {
		return usage;
	}

	/**
	 * Sets the Usage list of the price preview
	 * 
	 * @param usage The Usage list of the price preview
	 */
	public void setUsage(List<Usage> usage) {
		this.usage = usage;
	}

	/**
	 * Returns the BillingPreviewRequestDTO in json format
	 * 
	 * @return The Json (in string format) of the BillingPreviewRequestDTO
	 */
	public String toJson() {
		
		// productOrder
		String productOrderJson = this.getProductOrder().toJson();

		// usage list
		StringBuilder usageListJson = new StringBuilder("[");
		for (int i = 0; i < this.getUsage().size(); i++) {
			if (i > 0) {
				usageListJson.append(", ");
			}
			usageListJson.append(this.getUsage().get(i).toJson());
		}
		usageListJson.append("]");

		return "{ \"productOrder\": " + productOrderJson.toString() + ", \"usage\": " + usageListJson.toString() + "}";

	}
}