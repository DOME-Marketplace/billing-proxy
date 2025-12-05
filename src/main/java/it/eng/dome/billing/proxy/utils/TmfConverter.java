package it.eng.dome.billing.proxy.utils;

import it.eng.dome.billing.proxy.model.Money;
import jakarta.validation.constraints.NotNull;

public class TmfConverter {
	
	public static it.eng.dome.tmforum.tmf622.v4.model.Money convertMoneyTo622(@NotNull Money moneyIn){
		
		it.eng.dome.tmforum.tmf622.v4.model.Money  out=new it.eng.dome.tmforum.tmf622.v4.model.Money();
		
		out.setUnit(moneyIn.getUnit());
		out.setValue(moneyIn.getValue());
		
		return out;
		
	}

}
