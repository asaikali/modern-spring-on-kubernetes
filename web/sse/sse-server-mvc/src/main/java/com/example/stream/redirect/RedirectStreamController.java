package com.example.stream.redirect;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectStreamController {

  @GetMapping(path = "/test/redirect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<Void> redirect(@RequestParam(defaultValue = "AAPL") String symbol) {
    String targetUrl = "/mvc/stream/infinite?symbol=" + symbol;
    return ResponseEntity.status(302).header("Location", targetUrl).build();
  }
}
