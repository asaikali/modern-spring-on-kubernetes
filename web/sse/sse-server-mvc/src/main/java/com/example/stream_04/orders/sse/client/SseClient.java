// SseClient.java
package com.example.stream_04.orders.sse.client;

import java.io.InputStream;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * A simple SSE client using RestClient.
 * Delegates stream processing to SseStreamProcessor for clean separation of concerns.
 * 
 * Implements the client-side SSE processing per WHATWG HTML § 9.2 Server-sent events
 * https://html.spec.whatwg.org/multipage/server-sent-events.html
 */
public class SseClient {
    private final RestClient client;

    public SseClient(RestClient client) {
        this.client = client;
    }

    /**
     * Subscribe to the given SSE URI. For each event, your handler
     * is called with a RawSseEvent containing the raw text.
     * 
     * Uses SseStreamProcessor to handle the actual stream parsing,
     * while this class focuses on HTTP client concerns.
     * 
     * @param uri the SSE endpoint URI
     * @param handler callback invoked for each parsed SSE event
     */
    public void subscribe(String uri, Consumer<RawSseEvent> handler) {
        client.get()
            .uri(uri)
            .accept(MediaType.TEXT_EVENT_STREAM)  // § 9.2.5: MIME type is "text/event-stream"
            .exchange((req, resp) -> {
                try (
                    InputStream is = resp.getBody();
                    SseStreamProcessor processor = new SseStreamProcessor(is, handler)
                ) {
                    processor.processStream();
                } catch (Exception e) {
                    // Convert checked exceptions to runtime for simpler API
                    throw new RuntimeException("Failed to process SSE stream", e);
                }
                return null;
            });
    }
}
