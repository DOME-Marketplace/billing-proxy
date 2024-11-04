package it.eng.dome.billing.proxy.services;

import java.util.List;

import it.eng.dome.tmforum.tmf622.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.CustomerBill;

public interface BillingEngineService {
	
	public List<CustomerBill> computeBills(Product product) throws Exception;

}
