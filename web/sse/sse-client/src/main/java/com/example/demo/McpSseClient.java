package com.example.demo;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import org.springframework.core.ParameterizedTypeReference;

public class McpSseClient {

    private final WebClient client = WebClient.create("http://localhost:3001");

    public Flux<ServerSentEvent<String>> callMcpWithSse(String requestJson) {
        return client.post()
            .uri("/mcp")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .retrieve()
            .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});
    }

    public static void main(String[] args) {
        McpSseClient mcpClient = new McpSseClient();

        String initMessage = """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "initialize",
              "params": {
                "protocolVersion": "2025-06-18",
                "capabilities": {
                  "roots": {
                    "listChanged": true
                  },
                  "sampling": {},
                  "elicitation": {}
                },
                "clientInfo": {
                  "name": "ExampleClient",
                  "title": "Example Client Display Name",
                  "version": "1.0.0"
                }
              }
            }
            """;

        mcpClient.callMcpWithSse(initMessage)
            .subscribe(
                event -> {
                    System.out.println("=== Server Sent Event ===");
                    System.out.println("ID: " + event.id());
                    System.out.println("Event: " + event.event());
                    System.out.println("Retry: " + event.retry());
                    System.out.println("Data: " + event.data());

                    // Example: interact with fields
                    if ("special-event".equals(event.event())) {
                        System.out.println("Special event received, handling accordingly...");
                        // your custom logic here
                    }
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Stream completed")
            );

        // Keep JVM alive to process events (demo only)
        try { Thread.sleep(30000); } catch (InterruptedException e) {}
    }
}
