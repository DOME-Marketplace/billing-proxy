package it.eng.dome.billing.proxy.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.eng.dome.billing.proxy.exception.BillingProxyValidationException;
import it.eng.dome.billing.proxy.validator.TMFEntityValidator;
import it.eng.dome.brokerage.billing.dto.BillingRequestDTO;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.TimePeriod;
import jakarta.validation.constraints.NotNull;


@Service
public class BillingProxyService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
	
	@Autowired
	private TMFEntityValidator tmfEntityValidator;
	
	/**
	 * Creates a {@link BillingRequestDTO} to manage instant bill (i.e., the billingPeriod is set to now)
	 * 
	 * @param product The {@link Product} to bill now
	 * @return The BillingRequestDTO to manage instant bill
	 * @throws BillingProxyValidationException if a validation error of the Product occurs
	 */
	public BillingRequestDTO createBillingRequestDTOForInstantBill(@NotNull Product product) throws BillingProxyValidationException {

		OffsetDateTime now = OffsetDateTime.now();
		logger.info("Creating BillingRequestDTO for Product {} and dateTime {}", product, now.format(formatter));
		
		// Validate Product
		tmfEntityValidator.validateProduct(product);
		
		TimePeriod billingPeriod=new TimePeriod();
		billingPeriod.setStartDateTime(now);
		billingPeriod.setEndDateTime(now);
		
		return new BillingRequestDTO(product, billingPeriod, null);
		
	}
}
