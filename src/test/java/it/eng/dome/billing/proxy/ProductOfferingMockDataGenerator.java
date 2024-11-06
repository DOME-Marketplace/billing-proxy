package it.eng.dome.billing.proxy;

import java.net.URI;
import java.net.URISyntaxException;

import it.eng.dome.tmforum.tmf620.v4.ApiClient;
import it.eng.dome.tmforum.tmf620.v4.ApiException;
import it.eng.dome.tmforum.tmf620.v4.Configuration;
import it.eng.dome.tmforum.tmf620.v4.api.ProductOfferingApi;
import it.eng.dome.tmforum.tmf620.v4.api.ProductOfferingPriceApi;
import it.eng.dome.tmforum.tmf620.v4.api.ProductSpecificationApi;
import it.eng.dome.tmforum.tmf620.v4.model.CharacteristicValueSpecification;
import it.eng.dome.tmforum.tmf620.v4.model.Duration;
import it.eng.dome.tmforum.tmf620.v4.model.Money;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOffering;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingCreate;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPrice;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPriceCreate;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPriceRefOrValue;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPriceRelationship;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingTerm;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecification;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationCharacteristicValueUse;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationRef;

public class ProductOfferingMockDataGenerator {
	//private static String TMF_SERVER = "https://dome-dev.eng.it";
	//private static String OFFERING_PORT = "80";
	//private static String ORDERING_PORT = "80";
	
	private static String TMF_SERVER = "http://localhost";
	private static String CATALOG_PORT = "8100";


	public static void main(String[] args) throws URISyntaxException {
		try {
			ApiClient offeringClient = Configuration.getDefaultApiClient();
			offeringClient.setBasePath(TMF_SERVER + ":" + CATALOG_PORT + "/tmf-api/productCatalogManagement/v4");

			ProductSpecificationApi specApi = new ProductSpecificationApi(offeringClient);

			ProductSpecification ps = specApi.retrieveProductSpecification("cef424a9-abf8-44a4-b2bc-fda31dd36d6f", null);
			
		    (new ProductOfferingMockDataGenerator()).storeNewProductOffering(offeringClient, ps);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
		
	void storeNewProductOffering(ApiClient offeringClient, ProductSpecification ps) throws ApiException, URISyntaxException {
	    final var popApi = new ProductOfferingPriceApi(offeringClient);
		final var productSpecRef = new ProductSpecificationRef();
		
		System.out.println(ps.getHref().replace("https", "http"));
		productSpecRef.id(ps.getId()).href(new URI(ps.getHref()));
		
		ProductOfferingPriceCreate smallPriceCreate = createProductOfferingPriceSmall(productSpecRef);
		ProductOfferingPrice smallPrice = popApi.createProductOfferingPrice(smallPriceCreate);
		System.out.println("Creato POP small con id: " + smallPrice.getId() + ", href: " + smallPrice.getHref());
		
		ProductOfferingPriceCreate discountPopCreate = createSixMonthsDiscount();
		ProductOfferingPrice discountPop = popApi.createProductOfferingPrice(discountPopCreate);
		System.out.println("Creato POP discount con id: " + discountPop.getId() + ", href: " + discountPop.getHref());
		
		ProductOfferingPriceCreate largePriceCreate = createProductOfferingPriceLarge(productSpecRef, discountPop);
		ProductOfferingPrice largePrice = popApi.createProductOfferingPrice(largePriceCreate);
		System.out.println("Creato POP large con id: " + largePrice.getId() + ", href: " + largePrice.getHref());
		
		// Crea la Product Offering
	    final ProductOfferingApi offeringApi = new ProductOfferingApi(offeringClient);
	    ProductOfferingCreate poc = createProductOffering();
	    
	    var priceItem = new ProductOfferingPriceRefOrValue();
	    priceItem.href(new URI(smallPrice.getHref()));
	    priceItem.id(smallPrice.getId());
	    poc.addProductOfferingPriceItem(priceItem);
	    
	    priceItem = new ProductOfferingPriceRefOrValue();
	    priceItem.href(new URI(largePrice.getHref()));
	    priceItem.id(largePrice.getId());
	    poc.addProductOfferingPriceItem(priceItem);
	    
	    poc.setProductSpecification(productSpecRef);
	    
	    System.out.println(poc.toJson());

	    ProductOffering po = offeringApi.createProductOffering(poc);
		System.out.println("Creato ProductOffering con id: " + po.getId() + ", href: " + po.getHref());
	    
	    System.out.println(po.toJson());

	}
	
	private ProductOfferingCreate createProductOffering() {
		ProductOfferingCreate poc = new ProductOfferingCreate();
		
		poc
		.name("Your custom VM")
		.description("A customizable VM for your needs")
		.isBundle(false)
		.lifecycleStatus("Active");
		
		return poc;
	}
	
	private ProductOfferingPriceCreate createProductOfferingPriceSmall(ProductSpecificationRef productSpecRef) {
		Money price = new Money();
		price.value(24F).unit("EUR");
		
		ProductOfferingPriceCreate pop = new ProductOfferingPriceCreate();
		pop
		.name("SMALL Price")
		.description("4CPU, 8GB RAM, 20GB HD: 24 eu per m, recurring prepaid")
		.version("1.0")
		.priceType("recurring-prepaid")
		.recurringChargePeriodLength(1)
		.recurringChargePeriodType("month")
		.isBundle(false)
		.lifecycleStatus("Active")
		.price(price);
		
		{
			var cpuValue = new CharacteristicValueSpecification();
			cpuValue.isDefault(true).value(4);
			
			var cpuSpec = new ProductSpecificationCharacteristicValueUse();
			cpuSpec
			.name("CPU")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(cpuValue)
			.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(cpuSpec);
		}
		
		{
			var ramValue = new CharacteristicValueSpecification();
			ramValue.isDefault(true).value(8).unitOfMeasure("GB");
			
			var ramSpec = new ProductSpecificationCharacteristicValueUse();
			ramSpec
			.name("RAM Memory")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(ramValue)
			.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(ramSpec);
		}
		
		{
			var diskValue = new CharacteristicValueSpecification();
			diskValue.isDefault(true).value(20).unitOfMeasure("GB");
			
			var diskSpec = new ProductSpecificationCharacteristicValueUse();
			diskSpec
			.name("Storage")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(diskValue)
			.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(diskSpec);
		}
		
		return pop;
	}
	
	
	private ProductOfferingPriceCreate createSixMonthsDiscount() {
		ProductOfferingPriceCreate pop = new ProductOfferingPriceCreate();
		pop
		.name("Six months 50% special price")
		.description("Six months 50% special price")
		.version("1.0")
		.priceType("discount")
		.percentage(50F)
		.isBundle(false)
		.lifecycleStatus("Active");
				
		ProductOfferingTerm durationTerm = new ProductOfferingTerm();
		Duration duration = new Duration();
		duration.amount(6).units("month");
		durationTerm.setDuration(duration);
		
		pop.addProductOfferingTermItem(durationTerm);
		
		return pop;
	}
	
	
	private ProductOfferingPriceCreate createProductOfferingPriceLarge(ProductSpecificationRef productSpecRef,
			ProductOfferingPrice discountPop) throws URISyntaxException {
		Money price = new Money();
		price.value(30F).unit("EUR");
		
		ProductOfferingPriceCreate pop = new ProductOfferingPriceCreate();
		pop
		.name("Alessio Price LARGE")
		.description("8CPU, 16GB RAM, 30GB HD: 30 eu per m, recurring prepaid")
		.version("1.0")
		.priceType("recurring-prepaid")
		.recurringChargePeriodLength(1)
		.recurringChargePeriodType("month")
		.isBundle(false)
		.lifecycleStatus("Active")
		.price(price);
		
		{
			var cpuValue = new CharacteristicValueSpecification();
			cpuValue.isDefault(true).value(8);
			
			var cpuSpec = new ProductSpecificationCharacteristicValueUse();
			cpuSpec
			.name("CPU")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(cpuValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(cpuSpec);
		}
		
		{
			var ramValue = new CharacteristicValueSpecification();
			ramValue.isDefault(true).value(16).unitOfMeasure("GB");
			
			var ramSpec = new ProductSpecificationCharacteristicValueUse();
			ramSpec
			.name("RAM Memory")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(ramValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(ramSpec);
		}
		
		{
			var diskValue = new CharacteristicValueSpecification();
			diskValue.isDefault(true).value(30).unitOfMeasure("GB");
			
			var diskSpec = new ProductSpecificationCharacteristicValueUse();
			diskSpec
			.name("Storage")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(diskValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(diskSpec);
		}
		
		// Add discount
		if (discountPop != null) {
			ProductOfferingPriceRelationship discountRel = new ProductOfferingPriceRelationship();
			discountRel
			.relationshipType("discount")
			.href(new URI(discountPop.getHref()))
			.id(discountPop.getId());
			
			pop.addPopRelationshipItem(discountRel);
		}
		
		return pop;
	}
	
	
}
