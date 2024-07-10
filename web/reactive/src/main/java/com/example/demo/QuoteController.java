package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
  public class QuoteController {

    private final QuoteRepository quoteRepository;

    public QuoteController(QuoteRepository quoteRepository) {
      this.quoteRepository = quoteRepository;
    }

    @GetMapping("/random-quote")
    public Mono<Quote> randomQuote() {
      Quote quote = quoteRepository.findRandomQuote();
      if (quote != null) {
        return Mono.just(quote);
      } else {
        return Mono.empty();
      }
    }
  }
