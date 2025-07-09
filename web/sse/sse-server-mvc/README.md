Here is your **GitHub-friendly Markdown** version of the final HTML index:

---

# SSE Test Server - Endpoint Index

### MVC - Minimal Production-focused Endpoints
These examples demonstrate how to implement SSE endpoints using Spring MVC for typical app needs.

| Endpoint | Description |
| --- | --- |
| [`/mvc/stream/one`](http://localhost:8080/mvc/stream/one) | Sends one event then terminates. Suitable for one-shot notifications. |
| [`/mvc/stream/infinite`](http://localhost:8080/mvc/stream/infinite) | Never terminates; emits events at regular intervals. Suitable for ongoing data feeds. |

---

### WebFlux - Minimal Production-focused Endpoints
These examples demonstrate SSE implementation with reactive streams for high-concurrency apps.

| Endpoint | Description |
| --- | --- |
| [`/webflux/stream/one`](http://localhost:8080/webflux/stream/one) | Sends one event then terminates. Demonstrates Mono-based SSE. |
| [`/webflux/stream/infinite`](http://localhost:8080/webflux/stream/infinite) | Never terminates; emits events at regular intervals. Demonstrates Flux-based SSE. |

---

### Test - GET / streams
Classic use case: unsolicited server notifications (e.g. price updates, logs, chat messages). Includes edge cases for client resilience testing, idle timeout handling, partial events, and malformed streams.

| Endpoint | Description | Notes / Edge cases |
| --- | --- | --- |
| [`/test/stream/burst`](http://localhost:8080/test/stream/burst) | Sends burst of 1-25 events then closes | Batch notifications |
| [`/test/stream/none`](http://localhost:8080/test/stream/none) | Opens stream but never sends events | Tests client/proxy idle timeout behavior |
| [`/test/stream/bad`](http://localhost:8080/test/stream/bad) | Sends malformed SSE data | Missing data line, invalid UTF-8 |
| [`/test/stream/abrupt-close`](http://localhost:8080/test/stream/abrupt-close) | Starts sending then closes mid-event | Tests partial transmissions, network interruptions |
| [`/test/stream/delayed-first-event`](http://localhost:8080/test/stream/delayed-first-event) | Sends headers immediately, delays first event | Tests client idle detection |
| [`/test/stream/error-status`](http://localhost:8080/test/stream/error-status) | Returns non-200 status code | Tests client error handling on connect |
| [`/test/stream/redirect`](http://localhost:8080/test/stream/redirect) | Returns 302 redirect to another SSE endpoint | Tests client redirect support |
| [`/test/stream/retry-field`](http://localhost:8080/test/stream/retry-field) | Includes retry field in events | Tests reconnect interval respect |
| [`/test/stream/custom-event-types`](http://localhost:8080/test/stream/custom-event-types) | Sends events with custom event types | Tests client event type dispatch logic |

---

### Test - POST / RPC Streams
RPC style: inspired by MCP streamable HTTP. Client sends JSON RPC requests; server streams intermediate notifications, progress updates, and final completion messages. Essential for implementing structured long-running operations with streaming results.

| Endpoint | Description | Notes / Edge cases |
| --- | --- | --- |
| [`/test/rpc/long`](http://localhost:8080/test/rpc/long) | Streams notifications + final result | Client terminates upon result |
| [`/test/rpc/short`](http://localhost:8080/test/rpc/short) | Responds with single event result | Client or server terminates stream |
| [`/test/rpc/error`](http://localhost:8080/test/rpc/error) | Returns error response or SSE error event | Tests RPC error propagation |
| [`/test/rpc/none`](http://localhost:8080/test/rpc/none) | Opens stream but never sends events | Tests timeout for hanging RPC streams |
| [`/test/rpc/abrupt-close`](http://localhost:8080/test/rpc/abrupt-close) | Starts streaming then abruptly closes | Simulates server crash or network interruption |
| [`/test/rpc/delayed-first-event`](http://localhost:8080/test/rpc/delayed-first-event) | Sends headers immediately, delays first event | Tests client idle timeout settings |

---

Let me know if you want a **separate minimal version for README.md** focusing only on MVC and WebFlux teaching endpoints.
