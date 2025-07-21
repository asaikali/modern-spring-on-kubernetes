package com.example.stream_04.orders.sse.server;

public sealed interface ApiOutcome permits StreamReference, ImmediatePayload {}
