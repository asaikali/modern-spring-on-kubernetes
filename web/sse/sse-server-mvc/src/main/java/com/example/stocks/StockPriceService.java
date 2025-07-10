package com.example.stocks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StockPriceService {

  private final Random random = new Random();

  /** Gets current stock price - in real app this might call external API */
  public StockPrice getCurrentPrice(String symbol) {
    BigDecimal price =
        BigDecimal.valueOf(100 + random.nextDouble(10)).setScale(2, RoundingMode.HALF_UP);
    return new StockPrice(symbol, price, LocalDateTime.now());
  }

  /**
   * Reactive version that gets current stock price asynchronously.
   *
   * <p>In a real application with remote API calls, this would use WebClient:
   *
   * <pre>{@code
   * return webClient.get()
   *     .uri("/api/stocks/{symbol}", symbol)
   *     .retrieve()
   *     .bodyToMono(StockPrice.class)
   *     .timeout(Duration.ofSeconds(5))
   *     .onErrorReturn(new StockPrice(symbol, BigDecimal.ZERO, LocalDateTime.now()));
   * }</pre>
   *
   * For database calls, you'd use R2DBC:
   *
   * <pre>{@code
   * return stockRepository.findBySymbol(symbol)
   *     .map(entity -> new StockPrice(entity.getSymbol(), entity.getPrice(), entity.getTimestamp()));
   * }</pre>
   *
   * @param symbol Stock symbol to look up
   * @return Mono containing the stock price
   */
  public Mono<StockPrice> getCurrentPriceReactive(String symbol) {
    return Mono.fromCallable(
            () -> {
              // Simulate the same random price generation as blocking version
              BigDecimal price =
                  BigDecimal.valueOf(100 + random.nextDouble(10)).setScale(2, RoundingMode.HALF_UP);
              return new StockPrice(symbol, price, LocalDateTime.now());
            })
        .delayElement(Duration.ofMillis(50)); // Simulate network latency
  }
}
