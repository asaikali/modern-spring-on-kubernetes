package com.example.aot.runtimehints;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

public class Resource implements HelloService {

  private final org.springframework.core.io.Resource resource;

  public Resource(org.springframework.core.io.Resource resource) {
    this.resource = resource;
  }

  @Override
  public String sayHello(String name) {
    try {
      try (InputStream in = this.resource.getInputStream()) {
        String prefix = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        return sayHello(prefix, name);
      }
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to read resource " + resource, ex);
    }
  }
}
