package com.example.jackson.filter;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("userFilter")
public class User {

  private String username;
  private String email;
  private String secretNote;

  public User(String username, String email, String secretNote) {
    this.username = username;
    this.email = email;
    this.secretNote = secretNote;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSecretNote() {
    return secretNote;
  }

  public void setSecretNote(String secretNote) {
    this.secretNote = secretNote;
  }
}
