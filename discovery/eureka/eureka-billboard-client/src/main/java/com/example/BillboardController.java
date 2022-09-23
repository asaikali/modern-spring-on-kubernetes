package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BillboardController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/message")
    public String get(){
        Quote quote = restTemplate.getForObject("http://message-service/", Quote.class);
        return quote.getQuote() + " -- " + quote.getAuthor();
    }
}
