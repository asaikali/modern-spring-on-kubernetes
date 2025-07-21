package com.example.stream_04.orders;

public sealed interface Response permits EventualResponse, ImmediateResponse {}
