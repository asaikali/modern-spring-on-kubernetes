package com.example.stream_04.orders.sse.client;

import com.example.sse.RawSseEvent;
import com.example.sse.SseStreamProcessor;
import com.example.sse.SseStreamProcessor.ProcessingResult;
import java.util.function.Function;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * A simple SSE client using RestClient. Delegates stream parsing to SseStreamProcessor for clean
 * separation of concerns.
 *
 * <p>Implements the client-side SSE processing per WHATWG HTML § 9.2 Server-sent events
 * (https://html.spec.whatwg.org/multipage/server-sent-events.html), including: - § 9.2.5:
 * text/event-stream MIME type - § 9.2.6: line-by-line parsing and blank-line dispatch
 */
public class SseClient {
  private final RestClient client;

  public SseClient(RestClient client) {
    this.client = client;
  }

  /**
   * Subscribe to the given SSE URI. For each event, the handler is called with a RawSseEvent
   * containing the raw text. Processing is delegated to SseStreamProcessor.processStream().
   *
   * @param uri the SSE endpoint URI
   * @param handler callback invoked for each parsed SSE event; return false to stop streaming
   */
  public void subscribe(String uri, Function<RawSseEvent, ProcessingResult> handler) {
    client
        .get()
        .uri(uri)
        .accept(MediaType.TEXT_EVENT_STREAM) // § 9.2.5: MIME type must be text/event-stream
        .exchange(
            (httpRequest, clientHttpResponse) -> {
              SseStreamProcessor.parseStream(clientHttpResponse, handler, null, 1_000_000);
              return null;
            },
            false);
  }
}
