package it.eng.dome.billing.proxy.service;

public interface IProxyService {

	public String billingPreviewPrice(String order);

	public String invoicingPreviewTaxes(String order);

	public String bill(String bill);

	public String billApplyTaxes(String bill);

}
