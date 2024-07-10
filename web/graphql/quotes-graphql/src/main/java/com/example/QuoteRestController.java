package com.example;

import com.example.model.Quote;
import com.example.model.QuotesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteRestController {

  private final QuotesService quotesService;

  public QuoteRestController(QuotesService quotesService) {
    this.quotesService = quotesService;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {

    return this.quotesService.randomQuote();
  }
}
