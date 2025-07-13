package com.example.stream_04.orders;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCompleted (BuyOrder buyOrder, BigDecimal purchasePrice, Instant purchaseTime) {
}
