package it.eng.dome.billing.proxy.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

import it.eng.dome.billing.proxy.utils.HttpRequestFactory;
import it.eng.dome.tmforum.tmf622.v4.model.Product;
import it.eng.dome.tmforum.tmf678.v4.JSON;
import it.eng.dome.tmforum.tmf678.v4.model.CustomerBill;
import okhttp3.Call;
import okhttp3.Response;

@Component
public class DomeBillingEngineService implements BillingEngineService {

    private static final Logger log = LoggerFactory.getLogger(DomeBillingEngineService.class);

	// TODO le stringhe devono essere parametri di configurazione
	
	// necessita dell'endpoint dell'engine
	@Override
	public List<CustomerBill> computeBills(Product product) throws Exception {
		HttpRequestFactory requestFactory = new HttpRequestFactory();
		Call call = requestFactory.createPostRequest("localhost", "billing", product.toJson());
		Response response = call.execute();
		if (response.isSuccessful()) {
			Type listType = new TypeToken<List<CustomerBill>>(){}.getType();
			List<CustomerBill> newBills = JSON.getGson().fromJson(response.body().string(), listType);
			return newBills;
		} 
		
		return new ArrayList<CustomerBill>();
		// TODO: else log error and throw exception
	}

}
