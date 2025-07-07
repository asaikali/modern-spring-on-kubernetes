package com.example.demo.mcp;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class StreamableHttpClient {

  private final WebClient client;

  public StreamableHttpClient(String mcpServerUrl) {
    this.client = WebClient.builder().baseUrl(mcpServerUrl).build();
  }

  public McpResponse post(String requestJson) {
    ClientResponse response =
        client
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
            .bodyValue(requestJson)
            .exchange()
            .block();

    if (response == null) {
      throw new IllegalStateException("Failed to get response from server");
    }

    MediaType contentType =
        response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

    if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
      // Create the flux from the response body
      Flux<ServerSentEvent<String>> sseStream =
          response.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});

      // Return the SSE response with the flux
      return new SseMcpResponse(sseStream);

    } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      // For JSON responses, extract the body and return it
      return response.bodyToMono(String.class).map(JsonMcpResponse::new).block();

    } else {
      // Protocol violation: MCP must return JSON or SSE
      response.releaseBody(); // Release the response body to avoid memory leaks
      throw new IllegalStateException(
          "Invalid response Content-Type: "
              + contentType
              + ". MCP server must return application/json or text/event-stream.");
    }
  }
}
