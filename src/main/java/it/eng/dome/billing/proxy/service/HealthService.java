package it.eng.dome.billing.proxy.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.eng.dome.brokerage.observability.AbstractHealthService;
import it.eng.dome.brokerage.observability.health.Check;
import it.eng.dome.brokerage.observability.health.Health;
import it.eng.dome.brokerage.observability.health.HealthStatus;
import it.eng.dome.brokerage.observability.info.Info;

@Service
public class HealthService extends AbstractHealthService {

	private final Logger logger = LoggerFactory.getLogger(HealthService.class);
	private final static String SERVICE_NAME = "Billing Proxy";

    @Autowired
    private BillsService billingServices;
	
	
	@Override
	public Info getInfo() {

		Info info = super.getInfo();
		logger.debug("Response: {}", toJson(info));

		return info;
	}
	
	@Override
	public Health getHealth() {
		Health health = new Health();
		health.setDescription("Health for the " + SERVICE_NAME);

		health.elevateStatus(HealthStatus.PASS);

		// 1: check of Bills Service dependencies
		for (Check c : getBillsServiceCheck()) {
			health.addCheck(c);
			health.elevateStatus(c.getStatus());
		}

		// 2: check dependencies: in case of FAIL or WARN set it to WARN
		boolean onlyDependenciesFailing = health.getChecks("self", null).stream()
				.allMatch(c -> c.getStatus() == HealthStatus.PASS);
		
		if (onlyDependenciesFailing && health.getStatus() == HealthStatus.FAIL) {
	        health.setStatus(HealthStatus.WARN);
	    }

		// 3: check self info
		Check selfInfo = getChecksOnSelf(SERVICE_NAME);
		health.addCheck(selfInfo);
		health.elevateStatus(selfInfo.getStatus());
	    
	    // 4: build human-readable notes
	    health.setNotes(buildNotes(health));
		
		logger.debug("Health response: {}", toJson(health));
		
		return health;
	}
		
	private List<Check> getBillsServiceCheck() {

		List<Check> out = new ArrayList<>();

        Check invoicing = createCheck("invoicing-service", "connectivity", "external");

        try {
        	Info invoicingInfo = billingServices.getInfoInvoicingService();
        	invoicing.setStatus(HealthStatus.PASS);
        	invoicing.setOutput(toJson(invoicingInfo));
        }
        catch(Exception e) {
        	invoicing.setStatus(HealthStatus.FAIL);
        	invoicing.setOutput(e.getMessage());
        }
        out.add(invoicing);
        
        Check engine = createCheck("billing-engine", "connectivity", "external");

        try {
        	Info engineInfo = billingServices.getInfoBillingEngine();
        	engine.setStatus(HealthStatus.PASS);
        	engine.setOutput(toJson(engineInfo));
        }
        catch(Exception e) {
        	engine.setStatus(HealthStatus.FAIL);
        	engine.setOutput(e.getMessage());
        }
        out.add(engine);

		return out;
	}
}
