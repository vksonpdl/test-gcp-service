package com.example.gcp.testgcpservice.cntrlr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {
	


	
	@GetMapping("/")
	public Map<String, String> getHome() {
		
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "Success");
		
		
		
		return returnMap;
	}

}
