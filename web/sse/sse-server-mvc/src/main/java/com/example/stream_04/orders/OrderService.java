package com.example.stream_04.orders;

import com.example.stocks.StockPrice;
import com.example.stocks.StockPriceService;
import com.example.stream_04.orders.sse.server.ApiResponse;
import com.example.stream_04.orders.sse.server.SseEventId;
import com.example.stream_04.orders.sse.server.SseRabbitStreamManager;
import com.example.stream_04.orders.sse.server.SseStreamId;
import com.example.stream_04.orders.sse.server.SseStreamPublisher;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
class OrderService {

  private final Logger logger = LoggerFactory.getLogger(OrderService.class);
  private final StockPriceService stockPriceService;
  private final SseRabbitStreamManager sseRabbitStreamManager;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public OrderService(
      StockPriceService stockPriceService, SseRabbitStreamManager sseRabbitStreamManager) {
    this.stockPriceService = stockPriceService;
    this.sseRabbitStreamManager = sseRabbitStreamManager;
  }

  public SseEmitter resume(SseEventId lastEventId) {
    SseStreamPublisher sseStreamPublisher =
        this.sseRabbitStreamManager.createSsePublisher(lastEventId, "order-completed");
    return sseStreamPublisher.getSseEmitter();
  }

  public ApiResponse placeOrder(BuyOrder order) {

    StockPrice initialPrice = this.stockPriceService.getCurrentPrice(order.symbol());
    if (initialPrice.price().compareTo(order.maxPrice()) <= 0) {
      var orderCompleted = new OrderCompleted(order, initialPrice.price(), Instant.now());
      return new ApiResponse.Immediate(orderCompleted);
    }

    // generate a new stream id
    SseStreamId sseStreamId = SseStreamId.generate(order.symbol().toLowerCase());
    logger.info("Created new rabbitmq stream {} ", sseStreamId.fullName());

    this.executor.execute(
        () -> {
          try {

            try (var streamPublisher =
                this.sseRabbitStreamManager.createRabbitStreamPublisher(sseStreamId)) {
              while (true) {
                // Poll current price
                StockPrice price = stockPriceService.getCurrentPrice(order.symbol());
                BigDecimal current = price.price();
                // Check if we should complete the order
                if (current.compareTo(order.maxPrice()) <= 0) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                  var orderCompleted = new OrderCompleted(order, current, Instant.now());
                  boolean published = streamPublisher.publish(orderCompleted, "order-completed");
                  if (published) {
                    logger.info("Order completed for {} at price {}", order.symbol(), current);
                  } else {
                    // TODO retry the send to the RabbitMQ
                  }
                  break;
                }

                boolean published = streamPublisher.publish(price, "order-pending");
                if (published) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                } else {
                  // TODO retry the send to the RabbitMQ
                }

                // Wait before polling again
                Thread.sleep(Duration.ofSeconds(1));
              }
            }
          } catch (Exception e) {
            logger.error("Error in price polling loop for stream {}", sseStreamId.fullName(), e);
          }
        });

    var lastEventId = SseEventId.firstEvent(sseStreamId);
    return new ApiResponse.Stream(lastEventId);
  }
}
