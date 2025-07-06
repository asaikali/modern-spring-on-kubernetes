package com.example.demo.mcp;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public record SseMcpResponse(Flux<ServerSentEvent<String>> stream) implements McpResponse {}
