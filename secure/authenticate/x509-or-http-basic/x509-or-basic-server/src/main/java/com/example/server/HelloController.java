package com.example.server;

import jakarta.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/")
  public String hello(HttpServletRequest request, @AuthenticationPrincipal User user) {

    X509Certificate[] certificates =
        (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");

    if (certificates != null && certificates.length > 0) {
      X509Certificate clientCert = certificates[0];
      System.out.println("Client certificate: " + clientCert.getSubjectX500Principal().getName());
    } else {
      System.out.println("No client certificate found.");
    }

    boolean isSecure = request.isSecure();
    StringBuilder headers = new StringBuilder();
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      headers.append(headerName).append(": ").append(headerValue).append("\n");
    }

    return "Authenticated user: "
        + user.getUsername()
        + "\nHello time is: "
        + LocalDateTime.now()
        + "\n\n"
        + "Connection is secure: "
        + isSecure
        + "\n\n"
        + "HTTP Headers:\n\n"
        + headers.toString();
  }
}
