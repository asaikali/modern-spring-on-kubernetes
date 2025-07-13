package com.example.stream_04.orders;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/orders")
public class OrdersController {

  private final OrderService service;

  public OrdersController(OrderService service) {
    this.service = service;
  }

  /** POST /watchlist Creates a new stream for the requested symbol and returns an SSE stream. */
  @PostMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public Object subscribe(@RequestBody BuyOrder order, HttpServletResponse response) {
    var result = this.service.placeOrder(order);
    if (result instanceof  OrderCompleted) {
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(result);
    } else{
      response.setContentType("text/event-stream");
      return result;
    }
  }

  /** GET /watchlist/resume Resumes streaming using Last-Event-ID. */
  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter resume(@RequestHeader("Last-Event-ID") String lastEventId) {
    return null;
  }
}
