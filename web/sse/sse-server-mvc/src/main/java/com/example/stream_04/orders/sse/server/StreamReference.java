package com.example.stream_04.orders.sse.server;

public record StreamReference(SseEventId lastEventId) implements ApiOutcome {}
