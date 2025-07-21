package com.example.stream_04.orders.sse.server;

public sealed interface ApiResponse permits StreamApiResponse, ImmediateApiResponse {}
