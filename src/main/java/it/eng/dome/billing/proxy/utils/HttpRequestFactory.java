package it.eng.dome.billing.proxy.utils;

import java.nio.charset.Charset;
import java.util.Properties;

import lombok.NonNull;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequestFactory {
	
	private static final MediaType JSON = MediaType.get("application/json");
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String USER_AGENT_NAME = "User-Agent";
	private static final String USER_AGENT_VALUE = "OkHttp";

	public Call createPostRequest(@NonNull String baseUrl, @NonNull String path, @NonNull String body) {
		return this.createPostRequest(baseUrl, path, new Properties(), body);
	}
	
	
	public Call createPostRequest(@NonNull String baseUrl, @NonNull String path, 
			@NonNull Properties queryParams, @NonNull String body) {
		// Creates the URL with parameters
		HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/" + path).newBuilder();
		if (queryParams != null)
			queryParams.forEach((name, value) -> {urlBuilder.addQueryParameter(name.toString(), value.toString());});

		// Creates the URL with parameters
	    Request request = new Request.Builder()
	      .url(urlBuilder.build().toString())
	      .header(USER_AGENT_NAME, USER_AGENT_VALUE)
	      .addHeader(CONTENT_TYPE, JSON.toString())
	      .post(RequestBody.create(body.getBytes(Charset.forName("UTF-8")), JSON))
	      .build();
	    	    
	    // Creates the call
	    final OkHttpClient client = new OkHttpClient();
	    return client.newCall(request);
	}

	
	public Call createGetRequest(@NonNull String baseUrl, @NonNull String path,@NonNull  Properties queryParams) {
		// Creates the URL with parameters
		HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/" + path).newBuilder();
		if (queryParams != null)
			queryParams.forEach((name, value) -> {urlBuilder.addQueryParameter(name.toString(), value.toString());});

		// Creates the URL with parameters
	    Request request = new Request.Builder()
	      .url(urlBuilder.build().toString())
	      //.header(USER_AGENT_NAME, USER_AGENT_VALUE)
	      //.addHeader(<name>, <property>)
	      .build();
	    
	    // Creates the call
	    final OkHttpClient client = new OkHttpClient();
	    return client.newCall(request);
	}
}
