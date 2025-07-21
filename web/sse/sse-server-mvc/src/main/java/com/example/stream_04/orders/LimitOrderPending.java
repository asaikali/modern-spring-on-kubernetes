package com.example.stream_04.orders;

import com.example.stocks.StockPrice;

record LimitOrderPending(LimitOrderRequest limitOrderRequest, StockPrice currentPrice) {}
