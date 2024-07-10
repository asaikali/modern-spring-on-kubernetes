package com.example.model.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class AuthorEntity {
  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "wikipedia_url")
  private String wikipediaUrl;

  @Column(name = "field")
  private Integer field;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWikipediaUrl() {
    return wikipediaUrl;
  }

  public void setWikipediaUrl(String wikipediaUrl) {
    this.wikipediaUrl = wikipediaUrl;
  }

  public Integer getField() {
    return field;
  }

  public void setField(Integer field) {
    this.field = field;
  }
}
