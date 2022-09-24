package com.example;

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

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/")
  public Quote radomQuote() {
    logger.info("returning a random quote");
    return messageService.radomQuote();
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
