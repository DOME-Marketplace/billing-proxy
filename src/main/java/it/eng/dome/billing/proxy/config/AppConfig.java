package it.eng.dome.billing.proxy.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	// caddy-proxy attributes
	@Value("${proxy.proxyHost}")
	private String proxyHost;

	@Value("${proxy.proxyPort}")
	private int proxyPort;

	@Value("${proxy.proxyUser}")
	private String proxyUser;

	@Value("${proxy.proxyPassword}")
	private String proxyPassword;

	// register RestClient for serializing
	@Bean
	public RestClient restClient(ObjectMapper objectMapper) {

		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

		return RestClient.builder().messageConverters(converters -> {
			converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
			converters.add(jacksonConverter);
		}).build();
	}

	@Bean
	public RestClient restClientProxy(ObjectMapper objectMapper) {

		HttpHost proxy = new HttpHost("http", proxyHost, proxyPort);

		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

		credentialsProvider.setCredentials(new AuthScope(proxy),
				new UsernamePasswordCredentials(proxyUser, proxyPassword.toCharArray()));

		CloseableHttpClient httpClient = HttpClients.custom().setProxy(proxy)
				.setDefaultCredentialsProvider(credentialsProvider).build();

		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

		return RestClient.builder().requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
				.messageConverters(converters -> {
					converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
					converters.add(jacksonConverter);
				}).build();
	}
}
