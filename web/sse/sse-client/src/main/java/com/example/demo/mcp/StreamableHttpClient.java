package com.example.demo.mcp;

import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StreamableHttpClient {

  private final WebClient client;

  public StreamableHttpClient(String mcpServerUrl) {
    this.client = WebClient.builder().baseUrl(mcpServerUrl).build();
  }

  public McpResponse post2(String requestJson) {
    return client
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
        .bodyValue(requestJson)
        .exchangeToMono(
            response -> {
              MediaType contentType =
                  response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

              if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
                // Create the flux from the response body
                Flux<ServerSentEvent<String>> sseStream =
                    response
                        .bodyToFlux(new ParameterizedTypeReference<>() {})
                        .switchOnFirst(
                            (signal, objectFlux) -> {
                              if (signal.isOnNext()) {
                                ServerSentEvent<String> event =
                                    (ServerSentEvent<String>) signal.get();
                                // pare event data
                                // if its matching my criteria I am done
                                return Flux.just(event);
                              }

                              return Flux.empty();
                            });

                // Return the SSE response with the flux
                return Mono.just(new SseMcpResponse(sseStream));

              } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                // For JSON responses, extract the body and return it
                return response.bodyToMono(String.class).map(JsonMcpResponse::new);

              } else {
                // Protocol violation: MCP must return JSON or SSE
                response.releaseBody(); // Release the response body to avoid memory leaks
                throw new IllegalStateException(
                    "Invalid response Content-Type: "
                        + contentType
                        + ". MCP server must return application/json or text/event-stream.");
              }
            })
        .block();
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
