package com.example.number.mvc;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
public class SseStreamController {

  private static final Logger log = LoggerFactory.getLogger(SseStreamController.class);

  @GetMapping("/sse/numbers")
  public void streamNumbers(HttpServletRequest request, HttpServletResponse response) {

    AsyncContext asyncContext = request.startAsync();
    NumbersStreamDirectAsyncContext numbersStream =
        new NumbersStreamDirectAsyncContext(asyncContext);

    log.info("Controller streaming numbers");
    numbersStream.start();
    log.info("Exiting controller method");
  }

  @GetMapping("/sse/numbers/emitter")
  public ResponseEntity<ResponseBodyEmitter> streamNumbers() {
    log.info("Controller streaming numbers");

    NumbersResponseEmitterStream stream = new NumbersResponseEmitterStream();
    ResponseBodyEmitter emitter = stream.start();

    log.info("Exiting controller method");

    return ResponseEntity.ok()
        .header("Content-Type", "text/event-stream")
        .header("Cache-Control", "no-cache")
        .header("Connection", "keep-alive")
        .header("X-Accel-Buffering", "no") // Disable nginx buffering if behind proxy
        .body(emitter);
  }
}
