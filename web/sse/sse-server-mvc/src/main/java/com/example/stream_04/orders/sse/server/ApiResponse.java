package com.example.stream_04.orders.sse.server;


/**
 * <p>Represents the top-level sealed interface for the outcome of an API operation.
 * An API operation can either yield an immediate, synchronous data payload (e.g., a JSON object)
 * or provide a reference to an asynchronous Server-Sent Events (SSE) stream for ongoing updates.
 * </p>
 *
 * <p>This interface allows for a clear and type-safe way to handle the two distinct
 * response patterns from a single API endpoint:
 * <ul>
 * <li>{@link ImmediateApiResponse}: For responses where all necessary data is available
 * and returned immediately.</li>
 * <li>{@link StreamApiResponse}: For responses that initiate or refer to a continuous
 * stream of events, typically delivered via SSE.</li>
 * </ul>
 * </p>
 *
 * <p>Controllers and services can use a {@code switch} expression on an {@code ApiResponse}
 * instance to gracefully handle both scenarios.</p>
 */
public sealed interface ApiResponse permits StreamApiResponse, ImmediateApiResponse {}
