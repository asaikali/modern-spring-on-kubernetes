package com.example.stream_04.orders;

import java.math.BigDecimal;

record LimitOrderRequest(String symbol, Integer quantity, BigDecimal maxPrice) {}
