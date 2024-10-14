package it.eng.dome.billing.proxy.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "application/json")
	public Map<String, Object> helloWorld() {
		HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("date", new Date());
	    map.put("message", "BillingProxy is alive!");
	    return map;
	}
	
	/*
	 * Sample POST method
	@RequestMapping(value = "/greetings", method = RequestMethod.POST, consumes="application/json")
	public void addGreeting(@RequestBody ContentType type, Model model) {
	}
	*/

}
