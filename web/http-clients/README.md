# HTTP Clients

A collection of Spring Boot projects demonstrating how to make HTTP requests from Spring applications using various client types and patterns.

## Overview

This repository explores Spring's HTTP client capabilities through focused, hands-on examples. 
Each project is designed for quick experimentation and serves as a reference for common HTTP client patterns.

## The Three-Layer Architecture

Spring's HTTP client support follows a clean three-layer architecture that separates concerns and provides flexibility:

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│  RestClient, WebClient (your code uses these)               │
│  • High-level, user-friendly APIs                          │
│  • Built-in serialization/deserialization                  │
│  • Fluent, type-safe interfaces                            │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                   ABSTRACTION LAYER                         │
│   ClientHttpRequest, ClientHttpResponse, HttpHeaders        │
│   • Spring's HTTP abstractions                             │
│   • Protocol-agnostic interfaces                           │
│   • Interceptor and customization points                   │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                 IMPLEMENTATION LAYER                        │
│  Apache HttpClient, JDK HttpClient, Netty, OkHttp          │
│  • Actual HTTP libraries doing network calls               │
│  • Pluggable implementations                               │
│  • Connection pooling, SSL, low-level details              │
└─────────────────────────────────────────────────────────────┘
```

### Why This Architecture Matters

**Pluggable Implementations**: Switch between HTTP libraries (Apache HttpClient, JDK HttpClient, Netty) without changing your application code.
**Separation of Concerns**: Your business logic stays clean and focused while HTTP details are handled by the framework.
**Testability**: Mock at the abstraction layer for fast unit tests, or use real HTTP servers for integration tests.
**Performance Options**: Choose between blocking and reactive approaches based on your needs.
**Cross-Cutting Concerns**: Add authentication, logging, retries, and circuit breakers through interceptors and filters.

### Architecture Benefits

This three-layer design provides several key advantages:

**Consistency**: Whether you use RestClient or WebClient, the underlying concepts remain the same.
**Scalability**: Easy to switch from blocking to reactive as your application grows.
**Extensibility**: Add new capabilities through the abstraction layer without changing client code.
**Testability**: Multiple testing strategies available at different layers.
**Flexibility**: Choose the right tool for each use case while maintaining architectural consistency.
