package com.example.demo.discovery;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

public class DiscoveryRewriteInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    URI original = request.getURI();

    if ("my-service".equals(original.getHost())) {
      // Wrap the original request with a new URI
      HttpRequest newRequest =
          new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
              var uri =
                  UriComponentsBuilder.fromUri(original)
                      .host("jsonplaceholder.typicode.com")
                      .scheme("https")
                      .port(443)
                      .build(true)
                      .toUri();

              return uri;
            }
          };

      return execution.execute(newRequest, body);
    }

    // Default passthrough
    return execution.execute(request, body);
  }
}
