package com.example.stream_04.orders;

import java.math.BigDecimal;
import java.time.Instant;

record LimitOrderExecuted(
    LimitOrderRequest limitOrderRequest, BigDecimal purchasePrice, Instant purchaseTime)
    implements LimitOrderStatus {}
