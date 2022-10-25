package com.example;

import com.example.jpa.QuoteEntity;
import com.example.jpa.QuoteRepository;
import java.util.List;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
class QuoteController {

  private final QuoteRepository quoteRepository;

  QuoteController(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  @SchemaMapping(typeName = "Query", value = "randomQuote")
  QuoteEntity randomQuote() {
    return this.quoteRepository.findRandomQuote();
  }

  @QueryMapping(name = "all")
  List<QuoteEntity> all() {
    return this.quoteRepository.findAll();
  }
}
