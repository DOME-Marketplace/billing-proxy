package it.eng.dome.billing.proxy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.eng.dome.billing.proxy.utils.PriceTypeKey;
import it.eng.dome.billing.proxy.utils.TMForumEntityUtils;
import it.eng.dome.billing.proxy.model.Money;
import it.eng.dome.billing.proxy.utils.OrderPriceUtils;
import it.eng.dome.brokerage.model.PriceType;
import it.eng.dome.brokerage.model.RecurringChargePeriod;
import it.eng.dome.tmforum.tmf622.v4.model.OrderPrice;
import it.eng.dome.tmforum.tmf622.v4.model.Price;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrder;
import it.eng.dome.tmforum.tmf622.v4.model.ProductOrderItem;
import jakarta.validation.constraints.NotNull;

@Service
public class ProductOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
	
	public ProductOrder rebuildProductOrder(@NotNull ProductOrder productOrder, @NotNull List<ProductOrderItem> productOrderItems) {
		ProductOrder rebuildProductOrder=productOrder;
		
		this.uptateProductOrderItems(rebuildProductOrder, productOrderItems);
		
		List<OrderPrice> orderPrices=this.retriveItemPrices(productOrderItems);
		
		Map<PriceTypeKey, List<OrderPrice>> orderPriceGroups= this.generateOrderPriceGroups(orderPrices);
		
		// Calculates orderTotalPrice element
		if (rebuildProductOrder.getOrderTotalPrice() != null)
			rebuildProductOrder.setOrderTotalPrice(new ArrayList<OrderPrice>());
		
		// Calculate orderTotalPrice over groups aggregating for PriceTypeKey
		Set<PriceTypeKey> keys=orderPriceGroups.keySet();
		for(PriceTypeKey key: keys) {
			OrderPrice orderTotalPriceElement=calculateOrderTotalPriceElement(key,orderPriceGroups.get(key));
			rebuildProductOrder.addOrderTotalPriceItem(orderTotalPriceElement);
		}
		
		return rebuildProductOrder;
		
	}

	public ProductOrder uptateProductOrderItems(@NotNull ProductOrder productOrder, @NotNull List<ProductOrderItem> productOrderItems) {
		ProductOrder copy=productOrder;
		
		if (copy.getProductOrderItem() == null) {
			copy.setProductOrderItem(new ArrayList<ProductOrderItem>());
		}
		copy.setProductOrderItem(productOrderItems);
		
		return copy;
	}
	
	public List<ProductOrderItem> retrieveProductOrderItems(@NotNull List<ProductOrder> productOrders){
		List<ProductOrderItem> items=new ArrayList<ProductOrderItem>();
		
		for(ProductOrder productOrder:productOrders) {
			items.addAll(productOrder.getProductOrderItem());
		}
		
		return items;
	}
	
	public List<OrderPrice> retriveItemPrices(@NotNull List<ProductOrderItem> productOrderItems){
		List<OrderPrice> itemPrices=new ArrayList<OrderPrice>();
		
		for(ProductOrderItem productOrderItem: productOrderItems) {
			itemPrices.addAll(productOrderItem.getItemPrice());
		}
		
		return itemPrices;
	}
	
	// Generate an Hash Map to manage aggregation of the OrderPrice
	private Map<PriceTypeKey, List<OrderPrice>> generateOrderPriceGroups(@NotNull List<OrderPrice> orderPrices){
		HashMap<PriceTypeKey, List<OrderPrice>> orderPriceGroups=new HashMap<PriceTypeKey, List<OrderPrice>>();
		
		for(OrderPrice op:orderPrices) {
			PriceTypeKey key;

			PriceType priceType=OrderPriceUtils.getPriceType(op);
			if(priceType==PriceType.ONE_TIME||priceType==PriceType.DISCOUNT||priceType==PriceType.CUSTOM)
				key=new PriceTypeKey(priceType, null);
			else {
				RecurringChargePeriod rcp=OrderPriceUtils.getRecurrigChargePeriod(op);
				key=new PriceTypeKey(priceType, rcp);
			}
					
			if(orderPriceGroups.containsKey(key)) {
				orderPriceGroups.get(key).add(op);
			}else {
				List<OrderPrice> list=new ArrayList<OrderPrice>();
				list.add(op);
				orderPriceGroups.put(key, list);
			}
		}
		
		return orderPriceGroups;
	}
	
	/*
	 * Calculates the OrderPrice that will be added to the orderTotalPrice element of the ProductOrder aggregating according to the specified key.
	 * 
	 * @param key the PriceTypeKey considered for make the aggregation of the prices
	 * @return the OrderPrice item of the orderTotalPrice element of the ProductOrder
	 */
	private OrderPrice calculateOrderTotalPriceElement(PriceTypeKey key, List<OrderPrice> orderPrices) {
		logger.info("Calculate 'orderTotalPrice' for group with key "+key.toString());
		
		float orderTotalPriceAmount = 0F;
		String currency = null;
		
		// rounds order prices in a group to calculate the orderTotalPriceAmount
		for(OrderPrice op:orderPrices) {
			if(currency==null)
				currency=OrderPriceUtils.getCurrency(op);
			
			if (OrderPriceUtils.hasAlterations(op))
				orderTotalPriceAmount += OrderPriceUtils.getAlteredDutyFreePrice(op);
			else
				orderTotalPriceAmount += OrderPriceUtils.getDutyFreePrice(op);			
		}
		
		Money money=new Money(currency,orderTotalPriceAmount);
		Price orderTotalPrice=TMForumEntityUtils.createPriceTMF622(money);
		OrderPrice orderTotalPriceElement=TMForumEntityUtils.createOrderTotalPriceItemTMF622(orderTotalPrice, key);
		
		logger.info("Order total price: {} euro ", orderTotalPriceAmount);
		return orderTotalPriceElement;
	}

}
