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
     * Follows the SSE parsing algorithm from § 9.2.6 "Interpreting an event stream":
     * - Lines separated by blank lines form individual events
     * - UTF-8 decoding with optional BOM handling
     * - BufferedReader.readLine() handles CRLF/LF/CR line endings per Java spec
     */
    public void subscribe(String uri, Consumer<RawSseEvent> handler) {
        client.get()
            .uri(uri)
            .accept(MediaType.TEXT_EVENT_STREAM)  // § 9.2.5: MIME type is "text/event-stream"
            .exchange((req, resp) -> {
                try (
                  InputStream is = resp.getBody();
                  BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                ) {
                    StringBuilder rawBuf    = new StringBuilder();
                    String         line;
                    boolean        firstLine = true;

                    while ((line = br.readLine()) != null) {
                        // § 9.2.6: "The UTF-8 decode algorithm strips one leading UTF-8 Byte Order Mark (BOM), if any"
                        if (firstLine && line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        firstLine = false;

                        if (line.isEmpty()) {
                            // § 9.2.6: "If the line is empty (a blank line) - Dispatch the event"
                            if (rawBuf.length() > 0) {
                                // build and emit
                                handler.accept(new RawSseEvent(rawBuf.toString()));
                                rawBuf.setLength(0);
                            }
                        } else {
                            // accumulate this logical line
                            // Note: we append \n to normalize line endings as expected by parseFields()
                            rawBuf.append(line).append('\n');
                        }
                    }
                }
                return null;
            });
    }
}
