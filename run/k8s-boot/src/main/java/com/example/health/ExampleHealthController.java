package com.example.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleHealthController {

    private final ExampleHealthIndicator exampleHealthIndicator;

    public ExampleHealthController(ExampleHealthIndicator exampleHealthIndicator) {
        this.exampleHealthIndicator = exampleHealthIndicator;
    }

    @GetMapping("/status")
    public String get()
    {
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }

    @GetMapping("/fail")
    public String fail()
    {
        exampleHealthIndicator.setState(false);
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }

    @GetMapping("/pass")
    public String pass()
    {
        exampleHealthIndicator.setState(true);
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }
}
