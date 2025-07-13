package com.example.stream_04.orders;

import java.math.BigDecimal;

public record BuyOrder(String symbol, String quantity, BigDecimal maxPrice) {}
