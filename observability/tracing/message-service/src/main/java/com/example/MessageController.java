package com.example;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

  private final Logger logger = LoggerFactory.getLogger(MessageController.class);
  private final MessageService messageService;

  private final Tracer tracer;

  public MessageController(MessageService messageService, Tracer tracer) {
    this.messageService = messageService;

    // we don't need a tracer for normal app coding, we are using it here
    // to show how it can be used to access bagage that was sent over the wire from
    // client app
    this.tracer = tracer;
  }

  @GetMapping("/")
  public Quote randomQuote() {
    BaggageInScope baggage = this.tracer.getBaggage("billboardId");
    logger.info("baggage billboardId=" + baggage.get());

    logger.info("returning a random quote");

    return messageService.randomQuote();
  }

  @GetMapping("/quotes")
  public List<Quote> getAll() {
    logger.info("returning all quotes");
    return messageService.getAll();
  }

  @GetMapping("/quotes/{id}")
  public ResponseEntity<Quote> getQuote(@PathVariable("id") Integer id) {
    logger.info("looking for quote with id={}", id);
    Optional<Quote> quote = messageService.getQuote(id);
    if (quote.isPresent()) {
      return new ResponseEntity<>(quote.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
