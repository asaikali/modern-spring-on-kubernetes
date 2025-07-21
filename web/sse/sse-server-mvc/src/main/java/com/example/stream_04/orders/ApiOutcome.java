package com.example.stream_04.orders;

public sealed interface ApiOutcome permits EventualApiOutcome, ImmediateApiOutcome {}
