package com.example.stream.redirect;

import com.example.stream_02.prices.StockPrice;
import com.example.stream_02.prices.StockPriceService;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class RedirectStreamController {

  @GetMapping(path = "/test/redirect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<Void> redirect(@RequestParam(defaultValue = "AAPL") String symbol) {
    String targetUrl = "/mvc/stream/infinite?symbol=" + symbol;
    return ResponseEntity.status(302).header("Location", targetUrl).build();
  }

}
