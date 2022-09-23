package com.example;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "quotes")
public class Quote {
  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "quote")
  private String quote;

  @Column(name = "author")
  private String author;

  @Transient private boolean k8s = false;

  public boolean isK8s() {
    return k8s;
  }

  public void setK8s(boolean k8s) {
    this.k8s = k8s;
  }

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
    Quote quote1 = (Quote) o;
    return Objects.equals(id, quote1.id)
        && Objects.equals(quote, quote1.quote)
        && Objects.equals(author, quote1.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, quote, author);
  }
}
