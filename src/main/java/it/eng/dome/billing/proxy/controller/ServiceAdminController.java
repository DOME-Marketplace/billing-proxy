package it.eng.dome.billing.proxy.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class ServiceAdminController {

	private static final Logger log = LoggerFactory.getLogger(ServiceAdminController.class);

    @Value("${application.name}")
    private String appName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = "application/json")
    public Map<String, String> getInfo() {
        log.info("Request getInfo");
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", appName);
        map.put("version", buildVersion);
        map.put("timestamp", buildTimestamp);
        log.debug(map.toString());
        return map;
    }

}
