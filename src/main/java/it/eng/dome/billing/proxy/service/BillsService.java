package it.eng.dome.billing.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import it.eng.dome.brokerage.observability.info.Info;


@Service
public class BillsService {

	private static final Logger logger = LoggerFactory.getLogger(BillsService.class);

	private final String INVOICING_PATH_INFO = "/invoicing/info";
	private final String ENGINE_PATH_INFO = "/engine/info";

	@Value("${billing.invoicing_service}")
	public String invoicingServiceEndpoint;

	@Value("${billing.billing_engine}")
	public String billingEngineEndpoint;

	@Autowired
	RestClient restClient;


	/**
	 * Calls the Invoicing Service for getting info.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Info getInfoInvoicingService() throws Exception {
		try {
			
			ResponseEntity<Info> response = restClient.get()
					.uri(invoicingServiceEndpoint + INVOICING_PATH_INFO)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(Info.class);
			
			return response.getBody();
		} catch (Exception e) {
			logger.error("Exception calling invoicing service: ", e);
			throw (e);
		}
	}


	/**
	 * Calls the Billing Engine service for getting info.
	 * 
	 * @return Info
	 * @throws Exception
	 */
	public Info getInfoBillingEngine() throws Exception {
		try {
			ResponseEntity<Info> response = restClient.get()
					.uri(billingEngineEndpoint + ENGINE_PATH_INFO)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(Info.class);
			
			return response.getBody();
		} catch (Exception e) {
			logger.error("Exception calling invoicing service: ", e);
			throw (e);
		}
	}
}
