package com.example.jackson;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class NoSpacePrettyPrinter extends DefaultPrettyPrinter {

  public NoSpacePrettyPrinter() {
    this._objectFieldValueSeparatorWithSpaces = ": ";
  }

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new NoSpacePrettyPrinter();
  }
}
