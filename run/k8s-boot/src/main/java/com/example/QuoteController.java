package com.example;

import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

    private final QuoteRepository quoteRepository;
    private final Environment environment;

    public QuoteController(QuoteRepository quoteRepository, Environment environment) {
        this.quoteRepository = quoteRepository;
        this.environment = environment;
    }

    @GetMapping("/random-quote")
    public Quote randomQuote()
    {
        Quote result = quoteRepository.findRandomQuote();
        if(CloudPlatform.KUBERNETES.isActive(environment)) {
            result.setK8s(true);
        }
        return result;
    }
}
