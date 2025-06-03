package it.eng.dome.billing.proxy.tmf;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component(value = "tmfApiFactory")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class TmfApiFactory implements InitializingBean {
	
	private static final Logger log = LoggerFactory.getLogger(TmfApiFactory.class);
	private static final String TMF_ENDPOINT_CONCAT_PATH = "-";
	
    @Value("${tmforumapi.tmf_endpoint}")
    public String tmfEndpoint;
	
    @Value("${tmforumapi.tmf_envoy}")
    public boolean tmfEnvoy;
    
    @Value("${tmforumapi.tmf_namespace}")
    public String tmfNamespace;
    
    @Value("${tmforumapi.tmf_postfix}")
    public String tmfPostfix;    
    
    @Value("${tmforumapi.tmf_port}")
    public String tmfPort;
   
	@Value( "${tmforumapi.tmf620_catalog_path}" )
	private String tmf620CatalogPath;
	
	
	public it.eng.dome.tmforum.tmf620.v4.ApiClient getTMF620CatalogApiClient() {
		final it.eng.dome.tmforum.tmf620.v4.ApiClient apiClient = it.eng.dome.tmforum.tmf620.v4.Configuration.getDefaultApiClient();
		if (tmfEnvoy) {
			// usage of envoyProxy to access on TMForum APIs
			apiClient.setBasePath(tmfEndpoint + "/" + tmf620CatalogPath);
		}else {
			// use direct access on specific TMForum APIs software
			apiClient.setBasePath(tmfEndpoint + TMF_ENDPOINT_CONCAT_PATH + "product-catalog" + "." + tmfNamespace + "." + tmfPostfix + ":" + tmfPort + "/" + tmf620CatalogPath);
		}		
		log.debug("Invoke Catalog API at endpoint: " + apiClient.getBasePath());
		return apiClient;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Billing Engine is using the following TMForum endpoint prefix: " + tmfEndpoint);
		if (tmfEnvoy) {
			log.info("You set the apiProxy for TMForum endpoint. No tmf_port {} can be applied", tmfPort);	
		} else {
			log.info("No apiProxy set for TMForum APIs. You have to access on specific software via paths at tmf_port {}", tmfPort);	
		}
				
		Assert.state(!StringUtils.isBlank(tmfEndpoint), "Billing Scheduler not properly configured. tmf_endpoint property has no value.");
		Assert.state(!StringUtils.isBlank(tmf620CatalogPath), "Billing Scheduler not properly configured. The tmf620_catalog_path property has no value.");
		
		if (tmfEndpoint.endsWith("/")) {
			tmfEndpoint = removeFinalSlash(tmfEndpoint);		
		}
		
		if (tmf620CatalogPath.startsWith("/")) {
			tmf620CatalogPath = removeInitialSlash(tmf620CatalogPath);
		}
		
	}
	
	private String removeFinalSlash(String s) {
		String path = s;
		while (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		return path;
	}
	
	private String removeInitialSlash(String s) {
		String path = s;
		while (path.startsWith("/")) {
			path = path.substring(1);
		}				
		return path;
	}	

}
