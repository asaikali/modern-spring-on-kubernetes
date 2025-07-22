// RawSseEvent.java
package com.example.stream_04.orders.sse.client;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Immutable holder of the raw text of one SSE event—
 * exactly as it arrived, excluding the blank-line separator.
 */
public record RawSseEvent(String rawEvent) {

    /**
     * Holder for all parsed SSE fields per the spec.
     * - data: lines joined with '\n', trailing newline removed
     * - comments: lines joined with '\n', trailing newline removed
     */
    public static record Fields(
        String id,
        String event,
        Duration retry,
        String data,
        String comment
    ) {}

    /**
     * Parse the raw event text into its constituent fields in one pass.
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
            char c0 = line.charAt(0);
            switch (c0) {
                case ':' -> {
                    // comment line: drop leading ':' and accumulate
                    commentBuffer.append(line.substring(1).strip()).append('\n');
                }
                case 'd' -> {
                    if (line.startsWith("data:")) {
                        int start = 5;
                        if (line.length() > start && line.charAt(start) == ' ') start++;
                        dataBuffer.append(line.substring(start)).append('\n');
                    }
                }
                case 'e' -> {
                    if (line.startsWith("event:")) {
                        int start = 6;
                        if (line.length() > start && line.charAt(start) == ' ') start++;
                        eventType = line.substring(start);
                    }
                }
                case 'i' -> {
                    if (line.startsWith("id:")) {
                        int start = 3;
                        if (line.length() > start && line.charAt(start) == ' ') start++;
                        String v = line.substring(start);
                        if (!v.isEmpty() && v.indexOf('\u0000') < 0) {
                            eventId = v;
                        }
                    }
                }
                case 'r' -> {
                    if (line.startsWith("retry:")) {
                        int start = 6;
                        if (line.length() > start && line.charAt(start) == ' ') start++;
                        String v = line.substring(start);
                        boolean digits = !v.isEmpty();
                        for (int i = 0; i < v.length() && digits; i++) {
                            char d = v.charAt(i);
                            if (d < '0' || d > '9') digits = false;
                        }
                        if (digits) {
                            retryValue = Duration.ofMillis(Long.parseLong(v));
                        }
                    }
                }
                default -> {
                    // ignore other fields
                }
            }
        }
        // Remove trailing '\n'
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
     * @param bytes the UTF-8 encoded raw event text
     * @return a new RawSseEvent instance with raw text
     */
    public static RawSseEvent fromBytes(byte[] bytes) {
        String raw = new String(bytes, StandardCharsets.UTF_8);
        return new RawSseEvent(raw);
    }
}
