package com.example.demo.mcp;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StreamableHttpClient {

    private final WebClient client;

    public StreamableHttpClient(String mcpServerUrl) {
        this.client = WebClient.builder()
            .baseUrl(mcpServerUrl)
            .build();
    }

    public Mono<McpResponse> post(String requestJson) {
        return client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
            .bodyValue(requestJson)
            .exchangeToMono(this::handleResponse);
    }

    private Mono<McpResponse> handleResponse(ClientResponse response) {
        MediaType contentType = response.headers()
            .contentType()
            .orElse(MediaType.APPLICATION_OCTET_STREAM);

        if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
            Flux<ServerSentEvent<String>> sseStream = response.bodyToFlux(new ParameterizedTypeReference<>() {});
            return Mono.just(new SseMcpResponse(sseStream));

        } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return response.bodyToMono(String.class)
                .map(JsonMcpResponse::new);

        } else {
            // Protocol violation: MCP must return JSON or SSE
            return Mono.error(new IllegalStateException(
                "Invalid response Content-Type: " + contentType +
                ". MCP server must return application/json or text/event-stream."));
        }
    }
}
