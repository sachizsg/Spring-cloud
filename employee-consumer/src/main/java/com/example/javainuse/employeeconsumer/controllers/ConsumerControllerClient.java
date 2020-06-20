package com.example.javainuse.employeeconsumer.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

//Modify the ConsumerControllerClient class to autowired the DiscoveryClient dependency.
@Controller
public class ConsumerControllerClient {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	public void getEmployee() throws RestClientException, IOException {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("zuul-service");
		//List<ServiceInstance> instances=discoveryClient.getInstances("employee-producer");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		//--Request call using Zuul Gateway + Eureka
		baseUrl = baseUrl + "/producer/employee";
        
		//---Direct REST call without Zuul Gateway
		//baseUrl=baseUrl+"/employee";
		//String baseUrl = "http://localhost:8094/employee";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}
	
	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
}
