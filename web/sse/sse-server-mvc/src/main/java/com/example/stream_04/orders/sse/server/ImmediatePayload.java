package com.example.stream_04.orders.sse.server;

public record ImmediatePayload(Object result) implements ApiOutcome {}
