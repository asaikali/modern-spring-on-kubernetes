package com.example.stream_04.orders;

import com.example.stocks.StockPrice;
import com.example.stocks.StockPriceService;
import com.example.stream_04.orders.sse.ApiResponse;
import com.example.stream_04.orders.sse.server.RabbitSseBridge;
import com.example.stream_04.orders.sse.server.RabbitSseStreamFactory;
import com.example.stream_04.orders.sse.server.SseEventId;
import com.example.stream_04.orders.sse.server.SseStreamId;
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
  private final RabbitSseStreamFactory rabbitSseStreamFactory;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public OrderService(
      StockPriceService stockPriceService, RabbitSseStreamFactory rabbitSseStreamFactory) {
    this.stockPriceService = stockPriceService;
    this.rabbitSseStreamFactory = rabbitSseStreamFactory;
  }

  public SseEmitter resume(SseEventId lastEventId) {
    RabbitSseBridge rabbitSseBridge =
        this.rabbitSseStreamFactory.createRabbitSseBrdige(lastEventId, "order-executed");
    return rabbitSseBridge.getSseEmitter();
  }

  private LimitOrderStatus attemptBuy(LimitOrderRequest limitOrderRequest) {
    StockPrice currentPrice = stockPriceService.getCurrentPrice(limitOrderRequest.symbol());

    if (currentPrice.price().compareTo(limitOrderRequest.maxPrice()) <= 0) {
      return new LimitOrderExecuted(limitOrderRequest, currentPrice.price(), Instant.now());

    } else {
      return new LimitOrderPending(limitOrderRequest, currentPrice);
    }
  }

  public ApiResponse placeOrder(LimitOrderRequest order, boolean allowImmediate) {

    // see if we can buy the stock and avoid creating a stream
    LimitOrderStatus initialBuyAttemptStatus = attemptBuy(order);
    if (initialBuyAttemptStatus instanceof LimitOrderExecuted orderExecuted && allowImmediate) {
      return new ApiResponse.Immediate(orderExecuted);
    }

    // we need to stream the result, generate a new stream id
    SseStreamId sseStreamId = SseStreamId.generate(order.symbol().toLowerCase());
    logger.info("Created new rabbitmq stream {} ", sseStreamId.fullName());

    this.executor.execute(
        () -> {
          try {

            try (var streamPublisher =
                this.rabbitSseStreamFactory.createRabbitStreamPublisher(sseStreamId)) {

              var buyAttemptStatus = initialBuyAttemptStatus;
              do {

                String type =
                    switch (buyAttemptStatus) {
                      case LimitOrderExecuted orderExecuted -> "order-executed";
                      case LimitOrderPending orderPending -> "order-pending";
                    };

                boolean published = streamPublisher.publish(buyAttemptStatus, type);
                if (published) {
                  logger.info("published event: ", buyAttemptStatus);
                } else {
                  // TODO retry the send to the RabbitMQ
                }

                // Wait before attempting to buy
                Thread.sleep(Duration.ofSeconds(1));
                buyAttemptStatus = attemptBuy(order);
              } while (buyAttemptStatus instanceof LimitOrderPending);
            }
          } catch (Exception e) {
            logger.error("Error in price polling loop for stream {}", sseStreamId.fullName(), e);
          }
        });

    var lastEventId = SseEventId.firstEvent(sseStreamId);
    return new ApiResponse.Stream(lastEventId);
  }
}
