package com.example.model;

public enum Field {
  UNKNOWN(0),
  POLITICS(1),
  PHILOSOPHY(2),
  SPORTS(3),
  SCIENCE(4),
  ACTING(5);

  public final int value;

  Field(int v) {
    this.value = v;
  }

  public static Field of(int value) {
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return POLITICS;
      case 2:
        return SPORTS;
      case 3:
        return SCIENCE;
      case 5:
        return ACTING;
    }
    throw new IllegalArgumentException("Invalid value " + value);
  }
}
