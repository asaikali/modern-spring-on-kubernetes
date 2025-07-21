package com.example.stream_04.orders.sse.server;

import java.util.regex.Pattern;

/**
 * Results of validating a RabbitMQ stream name. Each value indicates whether the name is valid or
 * the specific rule that failed.
 */
public enum StreamNameValidationResult {
  VALID(""),
  NULL("Name cannot be null"),
  EMPTY("Name cannot be empty"),
  TOO_LONG("Name too long (maximum length is 255 characters)"),
  INVALID_CHARS("Invalid characters (only letters, digits, '.', '-', and '_' allowed)"),
  RESERVED_PREFIX("Name cannot start with the reserved prefix 'amq.'");

  private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9._-]+$");
  private final String message;

  StreamNameValidationResult(String message) {
    this.message = message;
  }

  /**
   * Obtains the validation result for the given stream name against RabbitMQ naming rules. This
   * method checks for null, empty, length, allowed characters, and reserved prefixes.
   *
   * @param name the stream name to validate
   * @return a StreamNameValidationResult value indicating success or the specific error
   */
  public static StreamNameValidationResult forStreamName(String name) {
    if (name == null) {
      return NULL;
    }
    if (name.isEmpty()) {
      return EMPTY;
    }
    if (name.length() > 255) {
      return TOO_LONG;
    }
    if (!VALID_CHARS.matcher(name).matches()) {
      return INVALID_CHARS;
    }

    // Check for the reserved "amq." prefix, case-insensitively.
    // Parameters for regionMatches:
    // 1. ignoreCase: true (perform a case-insensitive match)
    // 2. toffset: 0 (start comparison from the beginning of 'name')
    // 3. other: "amq." (the string to compare against)
    // 4. ooffset: 0 (start comparison from the beginning of "amq.")
    // 5. len: 4 (compare the first 4 characters, i.e., "amq.")
    if (name.regionMatches(true, 0, "amq.", 0, 4)) {
      return RESERVED_PREFIX;
    }
    return VALID;
  }

  /**
   * Checks if this validation result indicates a valid stream name.
   *
   * @return true if the validation result is VALID; false otherwise.
   */
  public boolean isValid() {
    return this == VALID;
  }

  /**
   * Retrieves a human-readable message describing the validation outcome.
   *
   * @return a descriptive message; empty if the name is valid.
   */
  public String getMessage() {
    return message;
  }
}
