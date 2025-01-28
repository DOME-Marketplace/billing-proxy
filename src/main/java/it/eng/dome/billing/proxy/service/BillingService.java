package it.eng.dome.billing.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.eng.dome.billing.proxy.tmf.TmfApiFactory;
import it.eng.dome.tmforum.tmf620.v4.api.ProductOfferingPriceApi;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPrice;


@Component(value = "billingService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingService implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(BillingService.class);
	
	@Autowired
	private TmfApiFactory tmfApiFactory;
	
	private ProductOfferingPriceApi productOfferingPrice;

	@Override
	public void afterPropertiesSet() throws Exception {
		productOfferingPrice = new ProductOfferingPriceApi(tmfApiFactory.getTMF620CatalogApiClient());		
	}

	public ProductOfferingPrice getProductOfferingPrice(String id) {
		try {
			return productOfferingPrice.retrieveProductOfferingPrice(id, null);
		} catch (it.eng.dome.tmforum.tmf620.v4.ApiException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
