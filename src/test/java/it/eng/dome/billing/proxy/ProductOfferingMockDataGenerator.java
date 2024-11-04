package it.eng.dome.billing.proxy;

import java.net.URI;
import java.net.URISyntaxException;

import it.eng.dome.tmforum.tmf620.v4.ApiClient;
import it.eng.dome.tmforum.tmf620.v4.ApiException;
import it.eng.dome.tmforum.tmf620.v4.Configuration;
import it.eng.dome.tmforum.tmf620.v4.api.ProductOfferingApi;
import it.eng.dome.tmforum.tmf620.v4.api.ProductOfferingPriceApi;
import it.eng.dome.tmforum.tmf620.v4.model.CharacteristicValueSpecification;
import it.eng.dome.tmforum.tmf620.v4.model.Money;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOffering;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingCreate;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPrice;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPriceCreate;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPriceRefOrValue;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationCharacteristicValueUse;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationRef;

public class ProductOfferingMockDataGenerator {
	
	public static void main(String[] args) throws URISyntaxException {
		try {
			(new ProductOfferingMockDataGenerator()).storeNewProductOffering();
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	void storeNewProductOffering() throws ApiException, URISyntaxException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
	    defaultClient.setBasePath("https://dome-dev.eng.it/tmf-api/productCatalogManagement/v4");
	    
	    final ProductOfferingPriceApi popApi = new ProductOfferingPriceApi(defaultClient);
	    
		var productSpecRef = new ProductSpecificationRef();
		productSpecRef.id("urn:ProductSpecification:12345").href(new URI("urn:ProductSpecification:12345"));
		
		ProductOfferingPriceCreate smallPriceCreate = createProductOfferingPriceSmall(productSpecRef);
		ProductOfferingPrice smallPrice = popApi.createProductOfferingPrice(smallPriceCreate);
		System.out.println("Creato POP small con id: " + smallPrice.getId() + ", href: " + smallPrice.getHref());
		
		ProductOfferingPriceCreate largePriceCreate = createProductOfferingPriceLarge(productSpecRef);
		ProductOfferingPrice largePrice = popApi.createProductOfferingPrice(largePriceCreate);
		System.out.println("Creato POP large con id: " + largePrice.getId() + ", href: " + largePrice.getHref());
		
	    final ProductOfferingApi offeringApi = new ProductOfferingApi(defaultClient);
	    ProductOfferingCreate poc = createProductOffering();
	    
	    var priceItem = new ProductOfferingPriceRefOrValue();
	    priceItem.href(new URI(smallPrice.getHref()));
	    priceItem.id(smallPrice.getId());
	    poc.addProductOfferingPriceItem(priceItem);
	    
	    priceItem = new ProductOfferingPriceRefOrValue();
	    priceItem.href(new URI(largePrice.getHref()));
	    priceItem.id(largePrice.getId());
	    poc.addProductOfferingPriceItem(priceItem);
	    
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
		.name("Alessio Price SMALL")
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
			.id("urn:Characteristic:1234")
			.name("CPU")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(cpuValue);
			// .setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(cpuSpec);
		}
		
		{
			var ramValue = new CharacteristicValueSpecification();
			ramValue.isDefault(true).value(8).unitOfMeasure("GB");
			
			var ramSpec = new ProductSpecificationCharacteristicValueUse();
			ramSpec
			.id("urn:Characteristic:5678")
			.name("RAM Memory")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(ramValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(ramSpec);
		}
		
		{
			var diskValue = new CharacteristicValueSpecification();
			diskValue.isDefault(true).value(20).unitOfMeasure("GB");
			
			var diskSpec = new ProductSpecificationCharacteristicValueUse();
			diskSpec
			.id("urn:Characteristic:09876")
			.name("Storage")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(diskValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(diskSpec);
		}
		
		return pop;
	}
	
	private ProductOfferingPriceCreate createProductOfferingPriceLarge(ProductSpecificationRef productSpecRef) {
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
			.id("urn:Characteristic:1234")
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
			.id("urn:Characteristic:5678")
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
			.id("urn:Characteristic:09876")
			.name("Storage")
			.valueType("number")
			.addProductSpecCharacteristicValueItem(diskValue);
			//.setProductSpecification(productSpecRef);
			
			pop.addProdSpecCharValueUseItem(diskSpec);
		}
		
		return pop;
	}
	
	
}
