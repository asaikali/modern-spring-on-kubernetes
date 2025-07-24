package com.example.stream_04.orders.sse.client;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class RawSseEventTest {

  @Test
  void constructorThrowsOnNullText() {
    assertThatThrownBy(() -> new RawSseEvent(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("text cannot be null or empty");
  }

  @Test
  void constructorThrowsOnEmptyText() {
    assertThatThrownBy(() -> new RawSseEvent(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("text cannot be null or empty");
  }

  @Test
  void parseFields_singleDataLine() {
    String payload = "data: hello world\n";
    RawSseEvent event = new RawSseEvent(payload);

    RawSseEvent.Fields f = event.parseFields();

    assertThat(f.id()).isNull();
    assertThat(f.event()).isNull();
    assertThat(f.retry()).isNull();
    assertThat(f.data()).isEqualTo("hello world");
    assertThat(f.comment()).isNull();
  }

  @Test
  void parseFields_multipleDataLinesAndNoTrailingNewline() {
    String payload = "data: line1\n" + "data: line2\n" + "data: line3\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    assertThat(f.data()).isEqualTo("line1\nline2\nline3");
  }

  @Test
  void parseFields_fullEvent() {
    String payload =
        ": this is a comment\n"
            + "data: first\n"
            + "data: second\n"
            + "event: customEvent\n"
            + "id: 42\n"
            + "retry: 1500\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    assertThat(f.comment()).isEqualTo("this is a comment");
    assertThat(f.data()).isEqualTo("first\nsecond");
    assertThat(f.event()).isEqualTo("customEvent");
    assertThat(f.id()).isEqualTo("42");
    assertThat(f.retry()).isEqualTo(Duration.ofMillis(1500));
  }

  @Test
  void parseFields_ignoresInvalidRetry() {
    String payload = "retry: notADigit\n" + "data: ok\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    assertThat(f.retry()).isNull();
    assertThat(f.data()).isEqualTo("ok");
  }

  @Test
  void parseFields_ignoresIdWithNullCharacter() {
    String payload = "id: good\u0000bad\n" + "data: payload\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    assertThat(f.id()).isNull();
    assertThat(f.data()).isEqualTo("payload");
  }

  @Test
  void toBytes_and_fromBytes_roundTrip() {
    String text = "data: roundtrip\n";
    RawSseEvent original = new RawSseEvent(text);

    byte[] bytes = original.toBytes();
    assertThat(bytes).isEqualTo(text.getBytes(StandardCharsets.UTF_8));

    RawSseEvent restored = RawSseEvent.fromBytes(bytes);
    assertThat(restored.text()).isEqualTo(text);
  }

  @Test
  void fromBytesThrowsOnNull() {
    assertThatThrownBy(() -> RawSseEvent.fromBytes(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bytes cannot be null");
  }

  @Test
  void parseFields_onlyBlankLines() {
    RawSseEvent.Fields f = new RawSseEvent("\n\n").parseFields();
    assertThat(f.id()).isNull();
    assertThat(f.event()).isNull();
    assertThat(f.retry()).isNull();
    assertThat(f.data()).isEmpty();
    assertThat(f.comment()).isNull();
  }

  @Test
  void parseFields_unknownFieldIsIgnored() {
    RawSseEvent.Fields f = new RawSseEvent("foo:bar\n").parseFields();
    assertThat(f.id()).isNull();
    assertThat(f.event()).isNull();
    assertThat(f.retry()).isNull();
    assertThat(f.data()).isEmpty();
    assertThat(f.comment()).isNull();
  }

  @Test
  void parseFields_dataWithoutColonOrValue() {
    // no colon
    RawSseEvent.Fields f1 = new RawSseEvent("data\n").parseFields();
    // colon but no value
    RawSseEvent.Fields f2 = new RawSseEvent("data:\n").parseFields();
    // colon + value without space
    RawSseEvent.Fields f3 = new RawSseEvent("data:value\n").parseFields();

    assertThat(f1.data()).isEmpty();
    assertThat(f2.data()).isEmpty();
    assertThat(f3.data()).isEqualTo("value");
  }

  @Test
  void parseFields_multipleComments() {
    String payload = ": first line\n:second line\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();
    assertThat(f.comment()).isEqualTo("first line\nsecond line");
  }

  @Test
  void parseFields_colonOnlyLine() {
    // yields one empty comment
    RawSseEvent.Fields f = new RawSseEvent(":\n").parseFields();
    assertThat(f.comment()).isEqualTo("");
  }

  @Test
  void parseFields_commentPreservesIndentBeyondOneSpace() {
    String payload = ":   indented\n";
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();
    // first space trimmed, two spaces remain
    assertThat(f.comment()).isEqualTo("  indented");
  }

  @Test
  void toBytes_and_fromBytes_handlesUnicode() {
    String text = "data: café\n";
    RawSseEvent orig = new RawSseEvent(text);
    RawSseEvent roundTrip = RawSseEvent.fromBytes(orig.toBytes());
    assertThat(roundTrip.text()).isEqualTo(text);
  }

  @Test
  void parseFields_preservesExtraSpaces() {
    String payload = "data:    something \n"; // four spaces before “something”, one trailing
    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    // Expect three leading spaces + “something ” exactly
    assertThat(f.data()).isEqualTo("   something ");
  }

  @Test
  void parseFields_complexMultiLineEvent_withTextBlocks() {
    String payload =
        """
        :This event demonstrates all the fields allowed by SSE events
        :payload is multi line notice how an SSE event can preserve formatting
        :Check the README.md file in the see folder for an explanation of SSE events
        :Event generated from a spring MVC controller /mvc/stream/one
        :Emitted from 'sse-scheduler-14' thread
        retry:5000
        id:event-1
        event:custom-event-type
        data:Line 1 of data
        data:   Line 2 of data indentation is preserved
        data:   all lines in this event are treated as part of the payload
        data:
        data:{"firstName":"John","lastName":"Doe"}
        data:
        data:{"firstName":"John","lastName":"Doe"}
        data:
        data:{
        data:  "firstName" : "John",
        data:  "lastName" : "Doe"
        data:}
        data:last data line
        """;

    RawSseEvent.Fields f = new RawSseEvent(payload).parseFields();

    // comments (stripTrailing to remove the final '\n')
    String expectedComment =
        """
        This event demonstrates all the fields allowed by SSE events
        payload is multi line notice how an SSE event can preserve formatting
        Check the README.md file in the see folder for an explanation of SSE events
        Event generated from a spring MVC controller /mvc/stream/one
        Emitted from 'sse-scheduler-14' thread
        """
            .stripTrailing();

    // data: JSON block properties should be indented by exactly one space
    String expectedData =
        """
        Line 1 of data
          Line 2 of data indentation is preserved
          all lines in this event are treated as part of the payload

        {"firstName":"John","lastName":"Doe"}

        {"firstName":"John","lastName":"Doe"}

        {
         "firstName" : "John",
         "lastName" : "Doe"
        }
        last data line
        """
            .stripTrailing();

    assertThat(f.retry()).isEqualTo(Duration.ofMillis(5000));
    assertThat(f.id()).isEqualTo("event-1");
    assertThat(f.event()).isEqualTo("custom-event-type");
    assertThat(f.comment()).isEqualTo(expectedComment);
    assertThat(f.data()).isEqualTo(expectedData);
  }
}
