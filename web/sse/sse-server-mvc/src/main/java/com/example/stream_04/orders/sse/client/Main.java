// Main.java (example usage)
package com.example.stream_04.orders.sse.client;

import org.springframework.web.client.RestClient;

public class Main {
    public static void main(String[] args) {
        // 1) Create default RestClient
        RestClient restClient = RestClient.create();

        // 2) Wrap it in our SseClient
        SseClient sse = new SseClient(restClient);

        // 3) Subscribe and handle each RawSseEvent
        sse.subscribe("http://localhost:8080/mvc/stream/infinite", rawEvent -> {
            System.out.println(">>> SSE event:");
            System.out.println(rawEvent.rawEvent());

            System.out.println(">>> Event Fields:");
            // Parse out all the fields in one pass:
            RawSseEvent.Fields fields = rawEvent.parseFields();
            System.out.println(fields);
            
            // Now access ID through fields if needed
            if (fields.id() != null) {
                System.out.println("Event ID: " + fields.id());
            }
        });
    }
}
