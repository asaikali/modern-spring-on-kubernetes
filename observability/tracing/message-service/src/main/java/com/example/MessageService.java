package com.example;

import io.micrometer.tracing.SpanName;
import io.micrometer.tracing.annotation.NewSpan;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class MessageService {
  private final Logger logger = LoggerFactory.getLogger(MessageController.class);
  private final QuoteRepository quoteRepository;

  public MessageService(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  @NewSpan
  @SpanName("radomQuote()")
  public Quote radomQuote() {
    logger.info("MessageService.radomQuote()");
    return quoteRepository.findRandomQuote();
  }

  @NewSpan
  @SpanName("radomQuote()")
  public List<Quote> getAll() {
    logger.info("MessageService.getAll()");
    return quoteRepository.findAll();
  }

  @NewSpan
  @SpanName("radomQuote()")
  public Optional<Quote> getQuote(@PathVariable("id") Integer id) {
    logger.info("MessageService.getQuote({})", id);
    return quoteRepository.findById(id);
  }
}
