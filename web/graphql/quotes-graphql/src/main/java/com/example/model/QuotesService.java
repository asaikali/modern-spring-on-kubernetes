package com.example.model;

import com.example.model.jpa.AuthorEntity;
import com.example.model.jpa.AuthorRepository;
import com.example.model.jpa.QuoteEntity;
import com.example.model.jpa.QuoteRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuotesService {
  private final QuoteRepository quoteRepository;
  private final AuthorRepository authorRepository;

  public QuotesService(QuoteRepository quoteRepository, AuthorRepository authorRepository) {
    this.quoteRepository = quoteRepository;
    this.authorRepository = authorRepository;
  }

  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public List<Quote> allQuotes() {
    return quoteRepository.findAll().stream()
        .map(this::fromQuoteEntity)
        .collect(Collectors.toList());
  }

  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public Quote randomQuote() {
    var quoteEntity = quoteRepository.findRandomQuote();
    return this.fromQuoteEntity(quoteEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Author addAuthor(Integer id, String name, String wikipediaUrl, String field) {
    AuthorEntity authorEntity = new AuthorEntity();
    authorEntity.setField(Field.valueOf(field).value);
    authorEntity.setName(name);
    authorEntity.setWikipediaUrl(wikipediaUrl);
    authorEntity.setId(id);

    this.authorRepository.save(authorEntity);

    return this.fromAuthorEntity(authorEntity);
  }

  private Quote fromQuoteEntity(QuoteEntity quoteEntity) {
    Quote quote = new Quote();
    quote.setId(quoteEntity.getId());
    quote.setQuote(quoteEntity.getQuote());

    var authorEntity = quoteEntity.getAuthor();
    var author = this.fromAuthorEntity(authorEntity);
    quote.setAuthor(author);

    return quote;
  }

  private Author fromAuthorEntity(AuthorEntity authorEntity) {
    var author = new Author();
    author.setId(authorEntity.getId());
    author.setName(authorEntity.getName());
    author.setField(Field.of(authorEntity.getField()));
    author.setWikipediaUrl(authorEntity.getWikipediaUrl());
    return author;
  }

  public List<Author> allAuthors() {
    return this.authorRepository.findAll().stream()
        .map(this::fromAuthorEntity)
        .collect(Collectors.toList());
  }
}
