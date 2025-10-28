package it.eng.dome.billing.proxy.validator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.eng.dome.billing.proxy.exception.BillingProxyValidationException;
import it.eng.dome.tmforum.tmf637.v4.model.Product;
import jakarta.validation.constraints.NotNull;

/**
 * Component to validate the TMForum Entities
 */
@Component
public class TMFEntityValidator {
	
	private final static Logger logger=LoggerFactory.getLogger(TMFEntityValidator.class);
	
	/**
	 * Checks the specified {@link Product} to verify that the required product's field for processing are present
	 * 
	 * @param prod The Product to check
	 * @throws BillingProxyValidationException if some expected product's field are missing
	 */
	public void validateProduct(@NotNull Product prod) throws BillingProxyValidationException {
		
		List<ValidationIssue> issues=new ArrayList<ValidationIssue>();
		
		if(prod.getProductPrice()==null || prod.getProductPrice().isEmpty()) {
			String msg=String.format("The Product '%s' must have 'ProductPrice'", prod.getId());
			issues.add(new ValidationIssue(msg,ValidationIssueSeverity.ERROR));
		}
		
		if(prod.getStartDate()==null){
			String msg=String.format("The Product '%s' must have 'startDate'", prod.getId());
			issues.add(new ValidationIssue(msg,ValidationIssueSeverity.ERROR));
		}
		
		
		if (issues.stream().anyMatch(i -> i.getSeverity() == ValidationIssueSeverity.ERROR)) {
            throw new BillingProxyValidationException(issues);
        }
		
		logger.debug("Validation of Product {} successful", prod.getId());
		
	}	
	

}
