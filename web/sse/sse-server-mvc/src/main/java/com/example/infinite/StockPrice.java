package com.example.infinite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockPrice(String symbol, BigDecimal price, LocalDateTime timestamp) {}
