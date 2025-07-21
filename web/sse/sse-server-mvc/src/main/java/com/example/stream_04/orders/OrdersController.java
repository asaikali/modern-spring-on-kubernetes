package com.example.stream_04.orders;

import com.example.stream_04.orders.sse.ApiResponse;
import com.example.stream_04.orders.sse.server.SseEventId;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/orders")
class OrdersController {

  private final OrderService orderService;

  public OrdersController(OrderService service) {
    this.orderService = service;
  }

  /** POST /watchlist Creates a new stream for the requested symbol and returns an SSE stream. */
  @PostMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public Object subscribe(@RequestBody LimitOrderRequest order, HttpServletResponse response, @RequestParam(required = false, defaultValue = "true") boolean allowImmediate) {
    var result = this.orderService.placeOrder(order, allowImmediate);
    return switch (result) {
      case ApiResponse.Immediate immediate ->
          ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(immediate.payload());

      case ApiResponse.Stream stream -> {
        response.setContentType("text/event-stream");
        yield orderService.resume(stream.lastEventId());
      }
    };
  }

  /** GET /watchlist/resume Resumes streaming using Last-Event-ID. */
  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter resume(@RequestHeader("Last-Event-ID") String lastEventId) {
    return orderService.resume(SseEventId.fromString(lastEventId));
  }
}
