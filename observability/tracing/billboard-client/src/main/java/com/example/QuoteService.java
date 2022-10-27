package com.example;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface QuoteService {

    @GetExchange
    Quote randomQuote();


    @GetExchange(url = "/quotes")
    List<Quote> getAllQuotes();

    @GetExchange(url = "/quotes/{id}")
    Quote getQuoteById(@PathVariable Integer id);
}
