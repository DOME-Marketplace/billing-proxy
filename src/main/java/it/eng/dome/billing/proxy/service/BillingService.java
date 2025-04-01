package it.eng.dome.billing.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.eng.dome.billing.proxy.tmf.TmfApiFactory;
import it.eng.dome.brokerage.api.ProductOfferingPriceApis;
import it.eng.dome.tmforum.tmf620.v4.model.ProductOfferingPrice;


@Component(value = "billingService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingService implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(BillingService.class);
	
	@Autowired
	private TmfApiFactory tmfApiFactory;
	
	private ProductOfferingPriceApis productOfferingPrice;

	@Override
	public void afterPropertiesSet() throws Exception {
		productOfferingPrice = new ProductOfferingPriceApis(tmfApiFactory.getTMF620CatalogApiClient());
	}

	public ProductOfferingPrice getProductOfferingPrice(String id) {
		logger.info("Get ProductOfferingPrice by Id: {}", id);
		return productOfferingPrice.getProductOfferingPrice(id, null);
	}
}
