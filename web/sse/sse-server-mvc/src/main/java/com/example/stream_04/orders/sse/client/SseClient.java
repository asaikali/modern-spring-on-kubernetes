// SseClient.java
package com.example.stream_04.orders.sse.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * A simple SSE client using RestClient.
 * Calls your handler for each completed event.
 */
public class SseClient {
    private final RestClient client;

    public SseClient(RestClient client) {
        this.client = client;
    }

    /**
     * Subscribe to the given SSE URI. For each event, your handler
     * is called with a RawSseEvent containing the raw text.
     */
    public void subscribe(String uri, Consumer<RawSseEvent> handler) {
        client.get()
            .uri(uri)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange((req, resp) -> {
                try (
                  InputStream is = resp.getBody();
                  BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                ) {
                    StringBuilder rawBuf    = new StringBuilder();
                    String         line;
                    boolean        firstLine = true;

                    while ((line = br.readLine()) != null) {
                        // Strip UTF-8 BOM on the very first line, if any
                        if (firstLine && line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        firstLine = false;

                        if (line.isEmpty()) {
                            // Blank line = end of one SSE event
                            if (rawBuf.length() > 0) {
                                // build and emit
                                handler.accept(new RawSseEvent(rawBuf.toString()));
                                rawBuf.setLength(0);
                            }
                        } else {
                            // accumulate this logical line
                            rawBuf.append(line).append('\n');
                        }
                    }
                }
                return null;
            });
    }
}
