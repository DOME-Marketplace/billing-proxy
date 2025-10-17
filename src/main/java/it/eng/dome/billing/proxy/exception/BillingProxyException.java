package it.eng.dome.billing.proxy.exception;

public class BillingProxyException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String message;

	public BillingProxyException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
