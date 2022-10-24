package com.example;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
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

  private final Tracer tracer;

  public MessageService(QuoteRepository quoteRepository, Tracer tracer) {
    this.quoteRepository = quoteRepository;
    this.tracer = tracer;
  }

  @Observed(
      name = "randomQuote",
      lowCardinalityKeyValues = {"country", "canada", "region", "east"})
  public Quote randomQuote() {
    BaggageInScope baggage = this.tracer.getBaggage("billboardId");
    logger.info("MessageService.randomQuote() billboardId=" + baggage.get());

    Quote quote = quoteRepository.findRandomQuote();

    this.tracer.currentSpan().tag("author", quote.getAuthor());

    return quote;
  }

  public List<Quote> getAll() {
    logger.info("MessageService.getAll()");
    return quoteRepository.findAll();
  }

  public Optional<Quote> getQuote(@PathVariable("id") Integer id) {
    logger.info("MessageService.getQuote({})", id);
    return quoteRepository.findById(id);
  }
}
