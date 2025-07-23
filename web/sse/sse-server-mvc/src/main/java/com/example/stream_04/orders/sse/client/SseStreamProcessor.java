// SseStreamProcessor.java
package com.example.stream_04.orders.sse.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Processes SSE streams from an InputStream, parsing events and calling handlers.
 * 
 * Implements the SSE parsing algorithm from WHATWG HTML § 9.2.6 "Interpreting an event stream":
 * - Lines separated by blank lines form individual events
 * - UTF-8 decoding with optional BOM handling
 * - BufferedReader.readLine() handles CRLF/LF/CR line endings per Java spec
 * 
 * This class is AutoCloseable to ensure proper resource cleanup.
 */
public class SseStreamProcessor implements AutoCloseable {
    private final InputStream inputStream;
    private final Consumer<RawSseEvent> eventHandler;
    private final BufferedReader reader;

    /**
     * Create a new SSE stream processor.
     * 
     * @param inputStream the input stream containing SSE data
     * @param eventHandler callback invoked for each parsed SSE event
     * @throws IllegalArgumentException if either parameter is null
     */
    public SseStreamProcessor(InputStream inputStream, Consumer<RawSseEvent> eventHandler) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        if (eventHandler == null) {
            throw new IllegalArgumentException("eventHandler cannot be null");
        }
        
        this.inputStream = inputStream;
        this.eventHandler = eventHandler;
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Process the SSE stream, calling the event handler for each complete event.
     * 
     * This method blocks until the stream ends or an IOException occurs.
     * Events are parsed according to the SSE specification and delivered to
     * the handler as RawSseEvent instances.
     * 
     * @throws IOException if an error occurs reading from the stream
     */
    public void processStream() throws IOException {
        StringBuilder rawBuf = new StringBuilder();
        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {
            // § 9.2.6: "The UTF-8 decode algorithm strips one leading UTF-8 Byte Order Mark (BOM), if any"
            if (firstLine && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            firstLine = false;

            if (line.isEmpty()) {
                // § 9.2.6: "If the line is empty (a blank line) - Dispatch the event"
                if (rawBuf.length() > 0) {
                    // build and emit event
                    eventHandler.accept(new RawSseEvent(rawBuf.toString()));
                    rawBuf.setLength(0);
                }
            } else {
                // accumulate this logical line
                // Note: we append \n to normalize line endings as expected by parseFields()
                rawBuf.append(line).append('\n');
            }
        }
    }

    /**
     * Close the underlying input stream and reader.
     * 
     * @throws IOException if an error occurs closing the stream
     */
    @Override
    public void close() throws IOException {
        try {
            reader.close();
        } finally {
            inputStream.close();
        }
    }
}
