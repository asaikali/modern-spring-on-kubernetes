// RawSseEvent.java
package com.example.stream_04.orders.sse.client;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Immutable holder of the raw text of one SSE event—
 * exactly as it arrived, excluding the blank-line separator.
 * 
 * Follows WHATWG HTML Living Standard § 9.2 Server-sent events
 * https://html.spec.whatwg.org/multipage/server-sent-events.html
 */
public record RawSseEvent(String rawEvent) {

    /**
     * Compact constructor for validation
     */
    public RawSseEvent {
        if (rawEvent == null || rawEvent.isEmpty()) {
            throw new IllegalArgumentException("rawEvent cannot be null or empty");
        }
    }

    /**
     * Holder for all parsed SSE fields per the spec.
     * - data: lines joined with '\n', trailing newline removed (§ 9.2.6)
     * - comments: lines joined with '\n', trailing newline removed
     * 
     * See WHATWG HTML § 9.2.6 "Interpreting an event stream"
     */
    public record Fields(
        String id,
        String event,
        Duration retry,
        String data,
        String comment
    ) {}

    /**
     * Parse the raw event text into its constituent fields per SSE spec.
     * 
     * Implements the field processing algorithm from WHATWG HTML § 9.2.6
     * "Interpreting an event stream" - specifically the steps for processing
     * fields after line parsing.
     */
    public Fields parseFields() {
        StringBuilder dataBuffer    = new StringBuilder();
        StringBuilder commentBuffer = new StringBuilder();
        String        eventType     = null;
        String        eventId       = null;
        Duration      retryValue    = null;

        String[] lines = rawEvent.split("\n");
        
        for (String line : lines) {
            if (line.isEmpty()) continue;
            
            // § 9.2.6: "If the line starts with a U+003A COLON character (:) - Ignore the line"
            if (line.startsWith(":")) {
                // Collect comments for debugging/logging purposes
                // Handle optional space after colon per spec pattern
                String commentText = line.substring(1);
                if (commentText.startsWith(" ")) commentText = commentText.substring(1);
                commentBuffer.append(commentText).append('\n');
                continue;
            }
            
            // Parse field name and value per SSE spec
            int colonIndex = line.indexOf(':');
            String fieldName = (colonIndex == -1) ? line : line.substring(0, colonIndex);
            
            // § 9.2.6: Extract field value, optionally skip one space
            // "If value starts with a U+0020 SPACE character, remove it from value"
            String fieldValue = "";
            if (colonIndex != -1) {
                fieldValue = line.substring(colonIndex + 1);
                if (fieldValue.startsWith(" ")) fieldValue = fieldValue.substring(1);
            }
            
            // § 9.2.6: Process field based on field name
            switch (fieldName) {
                case "data" -> {
                    // § 9.2.6: "Append the field value to the data buffer, 
                    // then append a single U+000A LINE FEED (LF) character"
                    dataBuffer.append(fieldValue).append('\n');
                }
                case "event" -> {
                    // § 9.2.6: "Set the event type buffer to the field value"
                    eventType = fieldValue;
                }
                case "id" -> {
                    // § 9.2.6: "If the field value does not contain U+0000 NULL, 
                    // then set the last event ID buffer to the field value"
                    if (fieldValue.indexOf('\u0000') < 0) {
                        eventId = fieldValue;
                    }
                }
                case "retry" -> {
                    // § 9.2.6: "If the field value consists of only ASCII digits, 
                    // then interpret the field value as an integer in base ten"
                    try {
                        // Long.parseLong() enforces "only ASCII digits" requirement
                        // and will throw for empty strings, non-digits, overflow, etc.
                        retryValue = Duration.ofMillis(Long.parseLong(fieldValue));
                    } catch (NumberFormatException e) {
                        // Per spec: "Otherwise, ignore the field"
                    }
                }
                default -> {
                    // § 9.2.6: "Otherwise - The field is ignored"
                }
            }
        }
        
        // § 9.2.6: Final data buffer processing per SSE specification
        // 
        // During field processing, each "data:" line gets appended to the data buffer
        // followed by a LINE FEED character (\n). This is required by the spec to
        // properly join multiple data lines. However, the spec also requires that
        // the final trailing newline be removed before dispatching the event.
        //
        // Example: 
        //   data: first line
        //   data: second line
        //   
        // Results in dataBuffer = "first line\nsecond line\n"
        // But the final event.data should be "first line\nsecond line" (no trailing \n)
        //
        // This ensures multi-line data is properly formatted while avoiding an
        // extra newline at the end that would not be present in the original message.
        //
        // "If the data buffer's last character is a U+000A LINE FEED (LF) character, 
        // then remove the last character from the data buffer"
        String data = dataBuffer.length() > 0
            ? dataBuffer.substring(0, dataBuffer.length() - 1)
            : "";
        String comment = commentBuffer.length() > 0
            ? commentBuffer.substring(0, commentBuffer.length() - 1)
            : null;

        return new Fields(eventId, eventType, retryValue, data, comment);
    }

    /**
     * Convert this raw event text to a UTF-8 byte array.
     */
    public byte[] toBytes() {
        return rawEvent.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Create a RawSseEvent from a UTF-8 byte array.
     * 
     * Utility method for serialization/deserialization scenarios.
     * Note: The SSE spec (§ 9.2.5) mandates UTF-8 encoding for event streams.
     *
     * @param bytes the UTF-8 encoded raw event text
     * @return a new RawSseEvent instance with raw text
     */
    public static RawSseEvent fromBytes(byte[] bytes) {
        String raw = new String(bytes, StandardCharsets.UTF_8);
        return new RawSseEvent(raw);
    }
}
