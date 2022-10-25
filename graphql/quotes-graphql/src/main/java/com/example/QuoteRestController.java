package com.example;

import com.example.jpa.QuoteEntity;
import com.example.jpa.QuoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteRestController {

  private final QuoteRepository quoteRepository;

  public QuoteRestController(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  @GetMapping("/random-quote")
  public QuoteEntity randomQuote() {
    return quoteRepository.findRandomQuote();
  }
}
