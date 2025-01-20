package it.eng.dome.billing.proxy.service;

import org.springframework.http.ResponseEntity;

public interface IProxyService {
	
	public ResponseEntity<String> previewPrice(String order);
	
	public ResponseEntity<String> bill(String billRequest);

}
