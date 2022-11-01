package com.example;

import com.example.model.Author;
import com.example.model.Quote;
import com.example.model.QuotesService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
class QuoteController {
  private final QuotesService quotesService;

  QuoteController(QuotesService quotesService) {
    this.quotesService = quotesService;
  }

  @SchemaMapping(typeName = "Query", value = "randomQuote")
  Quote randomQuote() {
    return this.quotesService.randomQuote();
  }

  @QueryMapping(name = "allQuotes")
  List<Quote> allQuotes() {
    return this.quotesService.allQuotes();
  }

  @QueryMapping(name = "allAuthors")
  List<Author> allAuthors() {
    return this.quotesService.allAuthors();
  }

  @MutationMapping
  Author addAuthor(
      @Argument Integer id,
      @Argument String name,
      @Argument String wikipediaUrl,
      @Argument String field) {
    return this.quotesService.addAuthor(id, name, wikipediaUrl, field);
  }
}
