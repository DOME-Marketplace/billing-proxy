package it.eng.dome.billing.proxy.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.eng.dome.billing.proxy.validator.ValidationIssue;

/**
 * Represents a BillingProxy validation exception, raised when an error occurs during the validation of the TMForum entities
 * managed during the BillingProxy processing
 */
public class BillingProxyValidationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final List<ValidationIssue> issues;

    public BillingProxyValidationException(List<ValidationIssue> issues) {
        super(buildMessage(issues));
        this.issues = issues;
    }
    
    public BillingProxyValidationException(ValidationIssue issue) {
        super(buildMessage(issue));
        this.issues = new ArrayList<ValidationIssue>();
        this.issues.add(issue);
    }
    

    public List<ValidationIssue> getIssues() {
        return issues;
    }

    private static String buildMessage(List<ValidationIssue> issues) {
        if (issues == null || issues.isEmpty()) {
            return "Validation successful: no issues provided";
        }

        String joinedIssues = issues.stream()
                .map(issue -> " - " + issue.toString())
                .collect(Collectors.joining("\n"));

        return "Validation failed:\n" + joinedIssues;
    }
    
    private static String buildMessage(ValidationIssue issue) {
    	
    	if (issue == null ) {
            return "Validation successful: no issue provided";
        }

        return "Validation failed:\n" + issue.toString();
    }
}

