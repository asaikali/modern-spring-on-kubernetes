package com.example.demo.orders;

public class Order {
  private final int id;
  private boolean flagged = false;

  public Order(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public boolean isFlagged() {
    return flagged;
  }

  public void setFlagged(boolean flagged) {
    this.flagged = flagged;
  }
}
