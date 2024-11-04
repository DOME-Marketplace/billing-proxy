package it.eng.dome.billing.proxy.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.eng.dome.billing.proxy.services.BillingEngineService;
import it.eng.dome.tmforum.tmf622.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.model.CustomerBill;

@RestController
@RequestMapping("/billing")
public class BillingController {
    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

	@Autowired
	private BillingEngineService billingEngine;

	@RequestMapping(value = "/bill", method = RequestMethod.POST, consumes="application/json")
	public List<CustomerBill> bill(@RequestBody Product product) throws Exception {
		return new ArrayList<CustomerBill>();
		/*
		 * TODO: 
		 * 1) a partire dal product ricevuto in input deve recuperare il
		 *    Provider che vende quel prodotto (probabilmente tramite
		 *    la relazione relatedParty)
		 * 2) recuperato il Provider tramite la sua configurazione deve 
		 *    capire quel Provider vuole usare il proprio billing engine
		 *    o quello di dome
		 */
		/*
		try {
			return billingEngine.computeBills(product);
		} catch (Exception e) {
			log.error("Errore nell'invocazione del Billing Engine", e);
			throw e;
		}
		*/
	}

}
