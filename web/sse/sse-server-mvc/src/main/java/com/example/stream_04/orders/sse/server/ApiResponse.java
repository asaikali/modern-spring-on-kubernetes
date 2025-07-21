package com.example.stream_04.orders.sse.server;

/**
 * Represents the top-level sealed interface for the outcome of an API operation. An API operation
 * can either yield an immediate, synchronous data payload (e.g., a JSON object) or provide a
 * reference to an asynchronous Server-Sent Events (SSE) stream for ongoing updates.
 *
 * <p>This interface allows for a clear and type-safe way to handle the two distinct response
 * patterns from a single API endpoint:
 *
 * <ul>
 *   <li>{@link Immediate}: For responses where all necessary data is available and returned
 *       immediately.
 *   <li>{@link Stream}: For responses that initiate or refer to a continuous stream of events,
 *       typically delivered via SSE.
 * </ul>
 *
 * <p>Controllers and services can use a {@code switch} expression on an {@code ApiResponse}
 * instance to gracefully handle both scenarios.
 */
public sealed interface ApiResponse permits ApiResponse.Immediate, ApiResponse.Stream {

  /**
   * Represents an immediate, synchronous data response from an API operation. This record is used
   * when the entire result of an API operation can be delivered in a single, non-streaming
   * response, typically as a JSON object.
   *
   * <p>It is a permitted implementation of the {@link ApiResponse} sealed interface.
   *
   * @param payload The actual data payload of the immediate response. This object is expected to be
   *     serializable into the desired response format (e.g., JSON) by the framework.
   */
  record Immediate(Object payload) implements ApiResponse {}

  /**
   * Represents an API response that provides a reference to a Server-Sent Events (SSE) stream. This
   * record is used when the result of an API operation involves continuous updates or a sequence of
   * events over time, rather than a single, immediate payload.
   *
   * <p>It serves as a signal to the client that an SSE connection should be established or resumed
   * using the provided stream identifier.
   *
   * <p>It is a permitted implementation of the {@link ApiResponse} sealed interface.
   *
   * @param lastEventId The {@link SseEventId} which uniquely identifies the specific SSE stream and
   *     indicates the last known event ID. This is crucial for clients to establish a new
   *     connection or resume a disconnected stream from the correct point using the {@code
   *     Last-Event-ID} HTTP header.
   */
  record Stream(SseEventId lastEventId) implements ApiResponse {}
}
