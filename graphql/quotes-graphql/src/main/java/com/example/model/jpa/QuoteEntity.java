package com.example.model.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "quotes")
public class QuoteEntity {
  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "quote")
  private String quote;

  @ManyToOne
  @JoinColumn(name = "author")
  private AuthorEntity author;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getQuote() {
    return quote;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public AuthorEntity getAuthor() {
    return author;
  }

  public void setAuthor(AuthorEntity author) {
    this.author = author;
  }
}
