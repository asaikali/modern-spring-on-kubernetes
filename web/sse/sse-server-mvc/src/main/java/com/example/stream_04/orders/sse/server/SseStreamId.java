package com.example.stream_04.orders.sse.server;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier for a stream within the system. A stream ID is a composite
 * identifier, ensuring a distinct and categorized reference for each stream.
 *
 * @param prefix A short, descriptive identifier for a category or type of stream. Must be lowercase
 *     alphanumeric (a–z, 0–9) and have a maximum length.
 * @param uuid The universally unique identifier that distinguishes this specific stream from all
 *     others.
 * @param fullName The complete, canonical representation of the stream ID, formed by combining the
 *     prefix and UUID.
 */
public record SseStreamId(String prefix, UUID uuid, String fullName) {

  private static final int MAX_PREFIX_LENGTH = 32;

  /**
   * Ensures the internal consistency and validity of a SseStreamId instance upon creation. This
   * constructor performs comprehensive validation on all provided components to guarantee that the
   * resulting SseStreamId object is well-formed and adheres to all rules.
   */
  public SseStreamId {
    // 1. Null Checks for all components
    Objects.requireNonNull(prefix, "Prefix cannot be null.");
    Objects.requireNonNull(uuid, "UUID cannot be null.");
    Objects.requireNonNull(fullName, "Full stream name cannot be null.");

    // 2. Prefix format validation
    if (!isValidPrefix(prefix)) {
      throw new IllegalArgumentException(
          "Prefix '%s' is invalid. Must be lowercase a–z and 0–9 only, and ≤ %d characters."
              .formatted(prefix, MAX_PREFIX_LENGTH));
    }

    // 3. Consistency check: fullName must be derived from prefix and uuid
    String derivedFullName = prefix + "." + uuid.toString();
    if (!fullName.equals(derivedFullName)) {
      throw new IllegalArgumentException(
          "Provided full name '%s' does not match derived name '%s' from prefix and UUID."
              .formatted(fullName, derivedFullName));
    }

    // 4. Overall stream name validation (e.g., length, reserved characters beyond prefix/uuid
    // format)
    var nameValidation = RabbitStreamNameValidationResult.forStreamName(fullName);
    if (!nameValidation.isValid()) {
      throw new IllegalArgumentException(
          "Invalid stream name: '%s' -> %s".formatted(fullName, nameValidation.getMessage()));
    }
  }

  @Override
  public String toString() {
    return fullName;
  }

  /**
   * Creates a new SseStreamId with the given prefix and a newly generated unique identifier. This
   * method ensures the new ID adheres to all structural and format requirements.
   *
   * @param prefix A string to categorize the stream. It must be lowercase alphanumeric (a–z, 0–9)
   *     and respect the maximum allowed length.
   * @return A new, unique SseStreamId instance.
   * @throws IllegalArgumentException if the provided prefix is invalid according to the naming
   *     rules.
   */
  public static SseStreamId generate(String prefix) {
    Objects.requireNonNull(prefix, "Prefix cannot be null.");

    if (!isValidPrefix(prefix)) {
      throw new IllegalArgumentException(
          "Prefix '%s' is invalid. Must be lowercase a–z and 0–9 only, and ≤ %d characters."
              .formatted(prefix, MAX_PREFIX_LENGTH));
    }

    UUID generatedUuid = UUID.randomUUID();
    String generatedFullName = prefix + "." + generatedUuid.toString();

    return new SseStreamId(prefix, generatedUuid, generatedFullName);
  }

  /**
   * Creates a SseStreamId by parsing its full string representation. The input string is expected
   * to be in the canonical "prefix.uuid" format, where 'prefix' adheres to specified rules and
   * 'uuid' is a standard UUID string.
   *
   * @param name The complete string representation of the stream ID (e.g.,
   *     "myprefix.a1b2c3d4-e5f6-7890-1234-567890abcdef").
   * @return A SseStreamId instance parsed from the given string.
   * @throws IllegalArgumentException if the provided name does not conform to the expected format
   *     or contains invalid prefix/UUID components.
   */
  public static SseStreamId fromString(String name) {
    Objects.requireNonNull(name, "Stream name cannot be null.");

    var validation = RabbitStreamNameValidationResult.forStreamName(name);
    if (!validation.isValid()) {
      throw new IllegalArgumentException(
          "Invalid stream name: '%s' -> %s".formatted(name, validation.getMessage()));
    }

    int dotIndex = name.lastIndexOf('.');
    if (dotIndex <= 0 || dotIndex == name.length() - 1) {
      throw new IllegalArgumentException(
          "Stream name must be in the form 'prefix.uuid': '%s'".formatted(name));
    }

    String parsedPrefix = name.substring(0, dotIndex);
    String uuidPart = name.substring(dotIndex + 1);

    if (!isValidPrefix(parsedPrefix)) {
      throw new IllegalArgumentException(
          "Parsed prefix '%s' is invalid. Must be lowercase a–z and 0–9 only, and ≤ %d characters."
              .formatted(parsedPrefix, MAX_PREFIX_LENGTH));
    }

    UUID parsedUuid;
    try {
      parsedUuid = UUID.fromString(uuidPart);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid UUID segment in stream name: '%s'".formatted(uuidPart), e);
    }

    return new SseStreamId(parsedPrefix, parsedUuid, name);
  }

  /**
   * Checks if a given string adheres to the defined rules for a valid SseStreamId prefix. A valid
   * prefix must consist only of lowercase alphanumeric characters (a–z, 0–9) and must not exceed
   * the maximum allowed length.
   *
   * @param prefix The string to validate as a prefix.
   * @return {@code true} if the string is a valid prefix, {@code false} otherwise.
   */
  private static boolean isValidPrefix(String prefix) {
    if (prefix == null || prefix.length() == 0 || prefix.length() > MAX_PREFIX_LENGTH) {
      return false;
    }

    for (int i = 0; i < prefix.length(); i++) {
      char c = prefix.charAt(i);
      if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))) {
        return false;
      }
    }
    return true;
  }
}
