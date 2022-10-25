package com.example.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "quotes")
public class QuoteEntity {
  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "quote")
  private String quote;

  @Column(name = "author")
  private String author;

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

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuoteEntity quote1 = (QuoteEntity) o;
    return Objects.equals(id, quote1.id)
        && Objects.equals(quote, quote1.quote)
        && Objects.equals(author, quote1.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, quote, author);
  }
}
