package com.example.infinite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public record StockPrice(String symbol, BigDecimal price, LocalDateTime timestamp) { }
