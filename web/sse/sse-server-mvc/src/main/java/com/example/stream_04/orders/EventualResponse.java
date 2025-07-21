package com.example.stream_04.orders;

import com.example.stream_04.orders.sse.server.SseEventId;

record EventualResponse( SseEventId lastEventId) implements Response {
}
