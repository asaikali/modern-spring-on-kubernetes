package com.example.stream_04.orders;

import com.example.stream_04.orders.sse.server.SseEventId;

public record EventualResponse( SseEventId lastEventId) implements Response {
}
