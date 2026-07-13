package com.example.jackson;

import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.core.util.Separators;

public class NoSpacePrettyPrinter extends DefaultPrettyPrinter {

  public NoSpacePrettyPrinter() {
    // Jackson 3 configures separators immutably through the constructor: keep the space after
    // the colon but drop the one before it (the default pretty printer writes " : ").
    super(Separators.createDefaultInstance().withObjectNameValueSpacing(Separators.Spacing.AFTER));
  }

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new NoSpacePrettyPrinter();
  }
}
