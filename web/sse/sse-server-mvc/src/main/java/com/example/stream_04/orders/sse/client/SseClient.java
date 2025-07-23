package com.example.stream_04.orders.sse.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * A simple SSE client using RestClient. Delegates stream parsing to SseStreamUtils for clean
 * separation of concerns.
 *
 * <p>Implements the client-side SSE processing per WHATWG HTML § 9.2 Server-sent events
 * (https://html.spec.whatwg.org/multipage/server-sent-events.html), including:
 *   - § 9.2.5: text/event-stream MIME type
 *   - § 9.2.6: line-by-line parsing and blank-line dispatch
 */
public class SseClient {
  private final RestClient client;

  public SseClient(RestClient client) {
    this.client = client;
  }

  /**
   * Subscribe to the given SSE URI. For each event, the handler is called with a RawSseEvent
   * containing the raw text. Processing is delegated to SseStreamUtils.processStream().
   *
   * @param uri the SSE endpoint URI
   * @param handler callback invoked for each parsed SSE event; return false to stop streaming
   */
  public void subscribe(String uri, SseEventHandler handler) {
    client
        .get()
        .uri(uri)
        .accept(MediaType.TEXT_EVENT_STREAM) // § 9.2.5: MIME type must be text/event-stream
        .exchange((req, resp) -> {
          try (
              BufferedReader reader = new BufferedReader(
                  new InputStreamReader(resp.getBody(), StandardCharsets.UTF_8)
              )
          ) {
            // Delegate parsing to the static utility method
            // maxEventChars bound prevents unbounded growth (character count guard)
            SseStreamUtils.processStream(reader, handler, 1_000_000);
          } catch (IOException e) {
            // Wrap in unchecked to simplify API
            throw new RuntimeException("Failed to process SSE stream", e);
          }
          return null;
        });
  }
}
