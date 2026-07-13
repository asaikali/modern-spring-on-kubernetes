package com.example.jackson.views;

import com.fasterxml.jackson.annotation.JsonView;

class User {

  @JsonView(Views.Public.class)
  private String username;

  @JsonView(Views.Internal.class)
  private String email;

  @JsonView(Views.Internal.class)
  private String secretNote;

  // Getters & Setters
  public String getUsername() {
    return username;
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getSecretNote() {
    return secretNote;
  }

  public User setSecretNote(String secretNote) {
    this.secretNote = secretNote;
    return this;
  }
}
