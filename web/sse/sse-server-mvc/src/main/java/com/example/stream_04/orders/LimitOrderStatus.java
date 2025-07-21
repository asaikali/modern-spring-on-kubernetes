package com.example.stream_04.orders;

sealed interface LimitOrderStatus permits LimitOrderExecuted, LimitOrderPending {}
