package com.example.stream_04.orders;

import com.example.stream_04.orders.sse.server.SseEventId;

public record EventualApiOutcome(SseEventId lastEventId) implements ApiOutcome {}
