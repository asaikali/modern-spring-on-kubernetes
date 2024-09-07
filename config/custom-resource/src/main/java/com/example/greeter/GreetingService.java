package com.example.greeter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
  private final GreetingInformer greetingInformer;

  public GreetingService(GreetingInformer greetingInformer) {
    this.greetingInformer = greetingInformer;
  }

  public List<Greeting> getAllGreetings() {
    List<Greeting> result =
        this.greetingInformer.getGreetings().stream()
            .map(
                object ->
                    new Greeting(object.getSpec().getMessage(), object.getSpec().getLanguage()))
            .toList();
    return result;
  }

  public String getGreetingMessage(String language) {
    Optional<GreetingResource> greetingResource = this.greetingInformer.getGreeting(language);
    Optional<String> greeting = greetingResource.map(object -> object.getSpec().getMessage());
    return greeting.orElse("Backup greeting in English");
  }
}
