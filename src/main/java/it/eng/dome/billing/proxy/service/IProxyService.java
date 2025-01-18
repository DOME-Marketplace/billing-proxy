package it.eng.dome.billing.proxy.service;

import org.springframework.http.ResponseEntity;

public interface IProxyService {
	
	public ResponseEntity<String> pricePreview(String appliedCustomerBillingRates);
	
	public ResponseEntity<String> bill(String billRequest);

}
