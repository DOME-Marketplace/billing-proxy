package it.eng.dome.billing.proxy.utils;

import it.eng.dome.billing.proxy.model.Money;
import it.eng.dome.tmforum.tmf622.v4.model.OrderPrice;
import it.eng.dome.tmforum.tmf622.v4.model.Price;
import jakarta.validation.constraints.NotNull;

public class TMForumEntityUtils {
	
	public static Price createPriceTMF622(@NotNull Money money) {
		Price price=new Price();
		price.setDutyFreeAmount(TmfConverter.convertMoneyTo622(money));
		price.setTaxIncludedAmount(null);
		return price;
	}
	
	public static OrderPrice createOrderTotalPriceItemTMF622(@NotNull Price price, @NotNull PriceTypeKey key) {
		OrderPrice op=new OrderPrice();
		
		op.setPriceType(key.getPriceType().toString());
		if(key.isPriceTypeInRecurringCategory()) {
			op.setRecurringChargePeriod(key.getRecurringChargePeriod().toString());
		}
		op.setPrice(price);
		
		return op;
	}
	
}
