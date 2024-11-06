package it.eng.dome.billing.proxy;

import it.eng.dome.tmforum.tmf622.v4.Configuration;
import it.eng.dome.tmforum.tmf622.v4.api.ProductOrderApi;
import it.eng.dome.tmforum.tmf622.v4.ApiClient;
import it.eng.dome.tmforum.tmf622.v4.ApiException;

public class ProductOrderingTester {

	public static void main(String[] args) {
	    try {
	    	ApiClient defaultClient = Configuration.getDefaultApiClient();
		    defaultClient.setBasePath("http://localhost:8000/tmf-api/productOrderingManagement/v4");
		    final ProductOrderApi poApi = new ProductOrderApi();

			System.out.println(poApi.listProductOrder("", 1, 100));
		} catch (ApiException e) {
			e.printStackTrace();
		}

	}

}
