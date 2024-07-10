package com.example.model;

public class Author {

  private Integer id;

  private String name;

  private String wikipediaUrl;

  private Field field;

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

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }
}
