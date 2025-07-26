package com.example.jackson.versioning;

public class User {
  private final int version;
  private final String name;
  private final int age;
  private final String email;

  public User(int version, String name, int age, String email) {
    this.version = version;
    this.name = name;
    this.age = age;
    this.email = email;
  }

  public int getVersion() {
    return version;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "User{"
        + "version="
        + version
        + ", name='"
        + name
        + '\''
        + ", age="
        + age
        + ", email='"
        + email
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof User other)) return false;
    return this.version == other.version
        && this.age == other.age
        && this.name.equals(other.name)
        && (this.email == null ? other.email == null : this.email.equals(other.email));
  }
}
