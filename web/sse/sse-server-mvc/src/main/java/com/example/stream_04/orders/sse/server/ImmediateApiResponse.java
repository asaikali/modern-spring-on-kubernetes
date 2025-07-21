package com.example.stream_04.orders.sse.server;

/**
 * <p>Represents an immediate, synchronous API response containing a direct data payload.
 * This class is used when the entire result of an API operation can be delivered
 * in a single, non-streaming response, typically as a JSON object.</p>
 *
 * <p>It is a permitted implementation of the {@link ApiResponse} sealed interface.</p>
 *
 * @param payload The actual data payload of the immediate response. This object
 * is expected to be serializable into the desired response format
 * (e.g., JSON) by the framework.
 */
public record ImmediateApiResponse(Object result) implements ApiResponse {}
