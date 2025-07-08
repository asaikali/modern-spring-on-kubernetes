package com.example.demo;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class MvcSseController2 {

  @GetMapping("/sse")
  SseEmitter sse() {
    SseEmitter emitter = new SseEmitter(60_000l);
    AtomicInteger count = new AtomicInteger();

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            () -> {
              try {
                SseEventBuilder builder =
                    SseEmitter.event()
                        .data(LocalDateTime.now())
                        .id(String.valueOf(count.getAndIncrement()))
                        .comment("example comment")
                        .reconnectTime(1000)
                        .name("time");

                emitter.send(builder);
              } catch (Exception e) {
                emitter.completeWithError(e);
              }
            },
            0,
            5,
            TimeUnit.SECONDS);

    emitter.onCompletion(() -> System.err.println("SSE stream closed"));
    emitter.onCompletion(() -> System.err.println("SSE emitter timed out"));
    return emitter;
  }
}
