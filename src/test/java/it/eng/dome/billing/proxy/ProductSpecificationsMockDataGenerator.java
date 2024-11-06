package it.eng.dome.billing.proxy;

import java.net.URISyntaxException;
import java.util.List;

import it.eng.dome.tmforum.tmf620.v4.ApiClient;
import it.eng.dome.tmforum.tmf620.v4.ApiException;
import it.eng.dome.tmforum.tmf620.v4.Configuration;
import it.eng.dome.tmforum.tmf620.v4.api.ProductSpecificationApi;
import it.eng.dome.tmforum.tmf620.v4.model.CharacteristicValueSpecification;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecification;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationCharacteristic;
import it.eng.dome.tmforum.tmf620.v4.model.ProductSpecificationCreate;

public class ProductSpecificationsMockDataGenerator {
	//private static String TMF_SERVER = "https://dome-dev.eng.it";
	//private static String OFFERING_PORT = "80";
	//private static String ORDERING_PORT = "80";
	
	private static String TMF_SERVER = "http://localhost";
	private static String CATALOG_PORT = "8100";


	public static void main(String[] args) throws URISyntaxException {
		try {
			ApiClient offeringClient = Configuration.getDefaultApiClient();
			offeringClient.setBasePath(TMF_SERVER + ":" + CATALOG_PORT + "/tmf-api/productCatalogManagement/v4");
			
		    (new ProductSpecificationsMockDataGenerator()).storeProductSpecifications(offeringClient);

			//ProductSpecificationApi specApi = new ProductSpecificationApi(offeringClient);
			//List<ProductSpecification> pss = specApi.listProductSpecification(null, null, null);
			//pss.forEach(ps -> {System.out.println(ps.getId());});
			//ProductSpecification ps = specApi.retrieveProductSpecification("45cee45c-20b4-4543-b13a-0d31844e455f", null);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	
	void storeProductSpecifications(ApiClient offeringClient) throws ApiException, URISyntaxException {
		ProductSpecificationCreate pSpec = new ProductSpecificationCreate();
		
		pSpec
		.name("Linux VM 2")
		.brand("FICODES")
		.productNumber("CSC-340-NGFW")
		.description("A Linux VM with multiple options")
		.isBundle(false)
		.lifecycleStatus("Active");
		
		// CPU
		{
			ProductSpecificationCharacteristic cpuChar = new ProductSpecificationCharacteristic();
			cpuChar
			.name("CPU")
			.configurable(true)
			.valueType("number");
			
			CharacteristicValueSpecification cpuValue1 = new CharacteristicValueSpecification();
			cpuValue1
			.isDefault(true)
			.value(4)
			.unitOfMeasure("CPU");
			cpuChar.addProductSpecCharacteristicValueItem(cpuValue1);
			
			CharacteristicValueSpecification cpuValue2 = new CharacteristicValueSpecification();
			cpuValue2
			.isDefault(false)
			.value(8)
			.unitOfMeasure("CPU");
			cpuChar.addProductSpecCharacteristicValueItem(cpuValue2);
			
			pSpec.addProductSpecCharacteristicItem(cpuChar);
		}
		
		// RAM
		{
			ProductSpecificationCharacteristic ramChar = new ProductSpecificationCharacteristic();
			ramChar
			.name("RAM")
			.configurable(true)
			.valueType("number");
			
			CharacteristicValueSpecification ramValue1 = new CharacteristicValueSpecification();
			ramValue1
			.isDefault(true)
			.value(8)
			.unitOfMeasure("GB");
			ramChar.addProductSpecCharacteristicValueItem(ramValue1);
			
			CharacteristicValueSpecification ramValue2 = new CharacteristicValueSpecification();
			ramValue2
			.isDefault(false)
			.value(16)
			.unitOfMeasure("GB");
			ramChar.addProductSpecCharacteristicValueItem(ramValue2);
			
			pSpec.addProductSpecCharacteristicItem(ramChar);
		}
		
		// Storage
		{
			ProductSpecificationCharacteristic storageChar = new ProductSpecificationCharacteristic();
			storageChar
			.name("Storage")
			.configurable(true)
			.valueType("number");
			
			CharacteristicValueSpecification storageValue1 = new CharacteristicValueSpecification();
			storageValue1
			.isDefault(true)
			.value(10)
			.unitOfMeasure("GB");
			storageChar.addProductSpecCharacteristicValueItem(storageValue1);
			
			CharacteristicValueSpecification storageValue2 = new CharacteristicValueSpecification();
			storageValue2
			.isDefault(false)
			.value(30)
			.unitOfMeasure("GB");
			storageChar.addProductSpecCharacteristicValueItem(storageValue2);
			
			CharacteristicValueSpecification storageValue3 = new CharacteristicValueSpecification();
			storageValue3
			.isDefault(false)
			.value(50)
			.unitOfMeasure("GB");
			storageChar.addProductSpecCharacteristicValueItem(storageValue3);
			
			pSpec.addProductSpecCharacteristicItem(storageChar);
		}		
		
		ProductSpecificationApi specApi = new ProductSpecificationApi(offeringClient);
		
		final ProductSpecification storedPS = specApi.createProductSpecification(pSpec);
		System.out.println(storedPS.toJson());
	}
	
}
