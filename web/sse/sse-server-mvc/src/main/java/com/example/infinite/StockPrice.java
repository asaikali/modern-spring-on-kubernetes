package com.example.infinite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public record StockPrice(String symbol, BigDecimal price, LocalDateTime timestamp) {

  // Sample stock symbols for demonstration
  private static final Random random = new Random();
  private static final List<String> stockSymbols = List.of("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA");

  public static StockPrice generateRandomStockPrice() {
    String symbol = stockSymbols.get(random.nextInt(stockSymbols.size()));

    // Generate realistic stock price between $50-500
    BigDecimal basePrice = BigDecimal.valueOf(50 + random.nextDouble() * 450);
    BigDecimal price = basePrice.setScale(2, RoundingMode.HALF_UP);

    return new StockPrice(symbol, price, LocalDateTime.now());
  }
}
