package com.example.stream_04.orders;

sealed interface Response permits EventualResponse, ImmediateResponse {}
