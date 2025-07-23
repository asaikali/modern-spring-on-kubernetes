package com.example.stream_04.orders.sse.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Processes a text/event-stream according to the WHATWG HTML Living Standard:
 *   §9.2.5 Parsing an event stream (stream production, ABNF),
 *   §9.2.6 Interpreting an event stream (field processing).
 * Last Updated 22 July 2025.
 *
 * Incomplete events at EOF (i.e., those not terminated by a blank line) are discarded per spec §9.2.5.
 */
public class SseStreamProcessor implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SseStreamProcessor.class);
    private static final int DEFAULT_MAX_EVENT_CHARS = 1_048_576; // 1M characters

    private final BufferedReader reader;
    private final SseEventHandler handler;
    private final String uri;
    private final int maxEventChars;
    private final AtomicLong eventsProcessed = new AtomicLong();
    private volatile boolean stopRequested = false;

    public SseStreamProcessor(InputStream is, SseEventHandler handler, String uri) {
        this(is, handler, uri, DEFAULT_MAX_EVENT_CHARS);
    }

    public SseStreamProcessor(InputStream is, SseEventHandler handler, String uri, int maxEventChars) {
        this.reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        this.handler = handler;
        this.uri = uri;
        this.maxEventChars = maxEventChars;
    }

    /**
     * Read lines, accumulate into events (terminated by blank line), and dispatch each RawSseEvent.
     * Incomplete buffer at EOF is discarded (no dispatch of unterminated event).
     * @throws IOException on I/O or oversize event
     */
    public void processStream() throws IOException {
        StringBuilder eventBuffer = new StringBuilder();
        String line;

        // §9.2.5: Strip one leading UTF-8 BOM (U+FEFF) if present
        reader.mark(1);
        int first = reader.read();
        if (first != 0xFEFF) {
            reader.reset(); // no BOM: rewind so first char is processed normally
        }

        while (!stopRequested && (line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                // Blank line: end of event --> dispatch
                RawSseEvent event = new RawSseEvent(eventBuffer.toString());
                boolean keepGoing;
                try {
                    keepGoing = handler.handle(eventsProcessed.getAndIncrement(), event);
                } catch (Exception e) {
                    logger.warn("Event handler threw exception for event {} from URI {}", eventsProcessed.get(), uri, e);
                    keepGoing = true;
                }
                if (!keepGoing) {
                    stopRequested = true;
                    break;
                }
                eventBuffer.setLength(0);
            } else {
                // Append line + '\n', with char-count guard
                if (eventBuffer.length() + line.length() + 1 > maxEventChars) {
                    throw new IOException("SSE event exceeds max chars of " + maxEventChars);
                }
                eventBuffer.append(line).append("\n");
            }
        }
        // Spec §9.2.5: do not dispatch incomplete events at EOF (omit final flush)
    }

    @Override
    public void close() {
        stopRequested = true;
        try {
            reader.close();
        } catch (IOException e) {
            logger.error("Error closing SSE stream reader for URI {}", uri, e);
        }
    }

    /**
     * External stop signal: closes reader to unblock readLine().
     */
    public void stop() {
        stopRequested = true;
        try {
            reader.close();
        } catch (IOException e) {
            logger.error("Error stopping SSE stream for URI {}", uri, e);
        }
    }
}
