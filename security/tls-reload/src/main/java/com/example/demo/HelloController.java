package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Provider;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/algs")
  public List<String> getSignatureAlgorithms() {
    List<String> algorithms = new ArrayList<>();
    for (Provider provider : Security.getProviders()) {
      for (Provider.Service service : provider.getServices()) {
        if ("Signature".equals(service.getType())) {
          algorithms.add(service.getAlgorithm());
        }
      }
    }
    return algorithms;
  }

  @GetMapping("/")
  public String hello(HttpServletRequest request) {
    boolean isSecure = request.isSecure();
    StringBuilder headers = new StringBuilder();
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      headers.append(headerName).append(": ").append(headerValue).append("\n");
    }

    return "\nHello time is: "
        + LocalDateTime.now()
        + "\n\n"
        + "Connection is secure: "
        + isSecure
        + "\n\n"
        + "HTTP Headers:\n\n"
        + headers.toString();
  }
}
