package com.example.demo.util;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Stream Name Validation Tests")
class StreamNameValidationResultTest {

  // Helper method for valid name assertions
  private void expectValid(String name) {
    StreamNameValidationResult actualResult = StreamNameValidationResult.forStreamName(name);
    assertThat(actualResult).isEqualTo(StreamNameValidationResult.VALID);
    assertThat(actualResult.isValid()).isTrue();
    assertThat(actualResult.getMessage()).isEmpty();
  }

  // Helper method for invalid name assertions
  private void expectInvalid(String name, StreamNameValidationResult expectedResult) {
    StreamNameValidationResult actualResult = StreamNameValidationResult.forStreamName(name);
    assertThat(actualResult).isEqualTo(expectedResult);
    assertThat(actualResult.isValid()).isFalse();
  }

  @Test
  @DisplayName("All valid stream names should pass validation")
  void testAllValidNames() {
    // Standard valid names
    expectValid("queue");
    expectValid("my-stream");
    expectValid("a1_b2.c3");
    expectValid("another.valid.name");

    // Valid name at maximum length (255 chars)
    String maxLengthValidName = "valid_name_" + "a".repeat(255 - "valid_name_".length());
    expectValid(maxLengthValidName);
  }

  @Test
  @DisplayName("All invalid stream names should be correctly identified")
  void testAllInvalidNames() {
    // Null Name
    expectInvalid(null, StreamNameValidationResult.NULL);

    // Empty Name
    expectInvalid("", StreamNameValidationResult.EMPTY);

    // Too Long Name (256 chars)
    String tooLongInvalidName = "too_long_" + "b".repeat(256 - "too_long_".length());
    expectInvalid(tooLongInvalidName, StreamNameValidationResult.TOO_LONG);

    // --- Invalid Characters ---
    expectInvalid("invalid@name", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("space name", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("exclaim!", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("name$", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my/stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my\\stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my!stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my*stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my(stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my+stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my=stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my{stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my;stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my:stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my\"stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my,stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my<stream", StreamNameValidationResult.INVALID_CHARS);
    expectInvalid("my?stream", StreamNameValidationResult.INVALID_CHARS);

    // --- Reserved Prefix ---
    expectInvalid("amq.queue", StreamNameValidationResult.RESERVED_PREFIX);
    expectInvalid("AMQ.stream", StreamNameValidationResult.RESERVED_PREFIX);
    expectInvalid("aMq.hello", StreamNameValidationResult.RESERVED_PREFIX);
    expectInvalid("amq.foo.bar.baz", StreamNameValidationResult.RESERVED_PREFIX);
  }
}
