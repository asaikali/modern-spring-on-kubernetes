package com.example.infinite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class StockPriceService {

  private final Random random = new Random();

  /** Gets current stock price - in real app this might call external API */
  public StockPrice getCurrentPrice(String symbol) {
    BigDecimal price =
        BigDecimal.valueOf(100 + random.nextDouble(10)).setScale(2, RoundingMode.HALF_UP);
    return new StockPrice(symbol, price, LocalDateTime.now());
  }
}
