package it.eng.dome.billing.proxy.validator;

/**
 * Class representing a validation issue with a message ad a severity
 */
public class ValidationIssue {
	
	private String message;
	
	private ValidationIssueSeverity severity;

	public ValidationIssue(String message, ValidationIssueSeverity severity) {
		super();
		this.message = message;
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ValidationIssueSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(ValidationIssueSeverity severity) {
		this.severity = severity;
	}
	
	@Override
    public String toString() {
        return String.format("[%s] %s", severity, message);
    }

}
