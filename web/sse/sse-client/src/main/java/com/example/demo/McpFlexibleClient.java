package com.example.demo;

import com.example.demo.mcp.*;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public class McpFlexibleClient {

    public static void main(String[] args) {
        StreamableHttpClient streamableHttpClient = new StreamableHttpClient("http://localhost:3001/mcp");

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

        McpResponse response = streamableHttpClient.post(initMessage);

        if (response instanceof JsonMcpResponse json) {
            System.out.println("JSON Response: " + json.json());

        } else if (response instanceof SseMcpResponse sse) {
            System.out.println("SSE Stream:");
            Flux<ServerSentEvent<String>> events = sse.stream();
            events.subscribe(event -> {
                System.out.println("ID: " + event.id());
                System.out.println("Data: " + event.data());
            });
        }

//        streamableHttpClient.post(initMessage)
//            .doOnNext(result -> {
//                if (result instanceof JsonMcpResponse json) {
//                    System.out.println("=== JSON Response ===");
//                    System.out.println(json.json());
//
//                } else if (result instanceof SseMcpResponse sse) {
//                    System.out.println("=== SSE Stream ===");
//                    sse.stream().subscribe(event -> {
//                        System.out.println("ID: " + event.id());
//                        System.out.println("Event: " + event.event());
//                        System.out.println("Retry: " + event.retry());
//                        System.out.println("Data: " + event.data());
//                    },
//                    error -> System.err.println("SSE error: " + error),
//                    () -> System.out.println("SSE stream completed"));
//                }
//            })
//            .block(); // block for demo only

        // 🔥 Add sleep here to keep JVM alive for SSE events
        try {
            Thread.sleep(30000); // sleep for 30 seconds to allow SSE processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
