package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuotesController {

    private final QuoteService quoteService;

    public QuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/random")
    Quote random() {
        return  this.quoteService.randomQuote();
    }

    @GetMapping("/all")
    List<Quote> all() {
        return this.quoteService.getAllQuotes();
    }

    @GetMapping("/quote/{id}")
    Quote getQuote(@PathVariable Integer id){
        return this.quoteService.getQuoteById(id);
    }
}
