package com.example.stream_02.prices;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockPrice(String symbol, BigDecimal price, LocalDateTime timestamp) {}
