package com.example.stream_03.watchlist;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/watchlist")
public class WatchListController {

  private final WatchListService service;

  public WatchListController(WatchListService service) {
    this.service = service;
  }

  /** POST /watchlist Creates a new stream for the requested symbol and returns an SSE stream. */
  @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@RequestBody WatchListRequest request) {
    return service.createWatchList(request.symbol());
  }

  /** GET /watchlist/resume Resumes streaming using Last-Event-ID. */
  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter resume(@RequestHeader("Last-Event-ID") String lastEventId) {
    return service.resumeWatchList(lastEventId);
  }
}
