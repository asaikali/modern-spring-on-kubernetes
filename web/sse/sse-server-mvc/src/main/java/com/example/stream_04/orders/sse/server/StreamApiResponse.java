package com.example.stream_04.orders.sse.server;

/**
 * <p>Represents an API response that provides a reference to an asynchronous Server-Sent Events (SSE) stream.
 * This class is used when the result of an API operation involves continuous updates or
 * a sequence of events over time, rather than a single, immediate payload.</p>
 *
 * <p>It serves as a signal to the client that an SSE connection should be established or
 * resumed using the provided stream identifier.</p>
 *
 * <p>It is a permitted implementation of the {@link ApiResponse} sealed interface.</p>
 *
 * @param lastEventId The {@link SseEventId} which uniquely identifies the specific SSE stream
 * and indicates the last known event ID. This is crucial for clients
 * to establish a new connection or resume a disconnected stream from
 * the correct point using the {@code Last-Event-ID} HTTP header.
 */
public record StreamApiResponse(SseEventId lastEventId) implements ApiResponse {}
