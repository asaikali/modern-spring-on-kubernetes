package com.example.stream_04.orders.sse.client;

import com.example.stream_04.orders.sse.client.RawSseEvent;
import com.example.stream_04.orders.sse.client.SseEventHandler;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes SSE streams from an InputStream, parsing events and calling handlers.
 */
public class SseStreamProcessor implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SseStreamProcessor.class);

    private final BufferedReader reader;
    private final SseEventHandler handler;
    private final String uri;
    private final int maxEventSize;
    private final AtomicLong eventIndex = new AtomicLong();
    private volatile boolean stopRequested = false;

    public SseStreamProcessor(InputStream inputStream, SseEventHandler handler, String uri) {
        this(inputStream, handler, uri, 1024 * 1024);
    }

    public SseStreamProcessor(InputStream inputStream, SseEventHandler handler, String uri, int maxEventSize) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.handler = handler;
        this.uri = uri;
        this.maxEventSize = maxEventSize;
    }

    /**
     * Reads the stream line by line, buffers events, and dispatches them to the handler.
     * Also dispatches a final buffered event if the stream ends without a trailing blank line.
     */
    public void processStream() throws IOException {
        StringBuilder eventBuffer = new StringBuilder();
        String line;

        while (!stopRequested && (line = reader.readLine()) != null) {
            // Strip BOM if present at start of stream
            if (eventIndex.get() == 0 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            // Blank line indicates end of one SSE event
            if (line.isEmpty()) {
                dispatchBufferedEvent(eventBuffer);
                eventBuffer.setLength(0);
                continue;
            }

            // Accumulate line with newline
            eventBuffer.append(line).append("\n");
            if (eventBuffer.length() > maxEventSize) {
                throw new IOException("SSE event size exceeded " + maxEventSize + " bytes");
            }
        }

        // ISSUE #1 FIX: If stream ends without a blank line, dispatch leftover buffer
        if (!stopRequested && eventBuffer.length() > 0) {
            dispatchBufferedEvent(eventBuffer);
        }
    }

    private void dispatchBufferedEvent(StringBuilder buffer) {
        long index = eventIndex.getAndIncrement();
        RawSseEvent event = new RawSseEvent(buffer.toString());
        try {
            boolean keepGoing = handler.handle(index, event);
            if (!keepGoing) {
                stop();
            }
        } catch (Exception e) {
            logger.warn("Event handler threw exception for event {} from URI {}", index, uri, e);
        }
    }

    /**
     * Request graceful stop: current event will complete, then reading halts.
     */
    public void stop() {
        stopRequested = true;
    }

    /**
     * Close the reader (and underlying stream); also signals stop.
     */
    @Override
    public void close() throws IOException {
        stopRequested = true;
        reader.close();
    }
}
