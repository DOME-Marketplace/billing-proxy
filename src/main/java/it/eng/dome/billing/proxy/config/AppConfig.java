package it.eng.dome.billing.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {


	// register RestTemplate for serializing
	@Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .forEach(c -> ((MappingJackson2HttpMessageConverter) c)
                .setObjectMapper(objectMapper));

        return restTemplate;
    }
}
