package com.example.stream_04.orders.sse.server;

public record StreamApiResponse(SseEventId lastEventId) implements ApiResponse {}
