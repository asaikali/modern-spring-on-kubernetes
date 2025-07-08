# Server-Sent Events (SSE) Fundamentals

## Overview

Server-Sent Events (SSE) enables a server to push real-time updates to clients over a single HTTP connection. Unlike WebSockets, SSE provides **unidirectional communication** from server to client, making it ideal for live notifications, dashboards, or monitoring feeds.

## Background

SSE was standardized as part of **HTML5 around 2010**, addressing the need for real-time web communication without complex polling or WebSocket overhead. It leverages existing HTTP infrastructure while providing native browser support via the EventSource API.

## The SSE Protocol

SSE uses a special MIME type `text/event-stream` and follows a simple text-based format. The server keeps an HTTP connection open and sends events as formatted text blocks.

### Basic Format Rules
- Each event is a block of text terminated by **two newlines** (`\n\n`)
- Field lines start with a field name, followed by a colon and the value
- Lines starting with `:` are comments (ignored by client)
- UTF-8 encoding is required

---

## Progressive Build-Up: HTTP Request Examples

Let's explore SSE by building up complexity step by step. Each example introduces one new field, showing how SSE events become more powerful.

### Example 1: Minimal SSE Event

The simplest possible SSE response contains just data.

**HTTP Request:**
```http
GET /api/events HTTP/1.1
Host: example.com
Accept: text/event-stream
```

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

data: Hello, SSE World!

```

**✅ What happens:**
- Server responds with `Content-Type: text/event-stream`
- The `data:` field contains the event payload
- Double newline (`\n\n`) signals the end of the event
- Client's `onmessage` handler receives: `"Hello, SSE World!"`

**Browser consumption:**
```javascript
const eventSource = new EventSource('/mvc/stream/minimal');

eventSource.onmessage = function(event) {
  console.log('Received:', event.data); // "Hello, SSE World!"
};
```

**What the browser does:**
1. **Establishes connection** - Opens HTTP connection with `Accept: text/event-stream`
2. **Parses SSE format** - Reads `data:` lines until double newline
3. **Creates MessageEvent** - Wraps the data in a JavaScript event object
4. **Triggers handler** - Calls `onmessage` since no custom event type specified
5. **Stays connected** - Keeps connection open for future events

### Example 2: Adding Event Type

**What if you need to send different types of events?** Consider these scenarios:

**User notifications:**
```
You have 3 new messages
```

**System alerts:**
```
Server will restart in 5 minutes for maintenance
```

**How do you handle these differently on the client?** One might show a subtle notification badge, while the other needs an urgent popup. Using `onmessage` for everything means you'd have to parse the content to figure out what type of event it is. **Event types** solve this elegantly.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

event: user-notification
data: You have 3 new messages


event: system-alert
data: Server will restart in 5 minutes for maintenance

```

**✅ What's new:**
- **`event:` field** sets a custom event type
- Each event type can be handled by a different client-side handler
- Event data is still available in `event.data`

**Browser consumption:**
```javascript
const eventSource = new EventSource('/mvc/stream/notifications');

// Handle user notifications - show subtle badge
eventSource.addEventListener('user-notification', function(event) {
    console.log('User notification:', event.data); // "You have 3 new messages"
    console.log('Event type:', event.type); // "user-notification"
    showNotificationBadge(event.data);
});

// Handle system alerts - show urgent popup
eventSource.addEventListener('system-alert', function(event) {
    console.log('System alert:', event.data); // "Server will restart in 5 minutes..."
    console.log('Event type:', event.type); // "system-alert"
    showUrgentPopup(event.data);
});

// This won't be called anymore since we're using custom event types
eventSource.onmessage = function(event) {
    console.log('Default message:', event.data);
};
```

**What's different:**
- **Different event types** get routed to **different handlers**
- No need to parse message content to determine how to handle it
- `onmessage` is only called for events without an `event:` field
- Each event type can have its own processing logic and UI behavior

### Example 3: Adding Event ID

**What if the connection drops and you miss critical events?** Consider these scenarios:

**Financial trading alerts:**
```
AAPL stock hit $150 - trigger sell order
```

**Order processing updates:**
```
Order #12345 has been shipped - tracking: 1Z999AA1234567890
```

**How do you ensure no critical events are lost during network hiccups or server restarts?** Without event IDs, a dropped connection means lost events - potentially missing a trade execution or shipment notification. **Event IDs** enable reliable resumption.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: trade-001
event: trading-alert
data: AAPL stock hit $150 - trigger sell order


id: order-002
event: order-update
data: Order #12345 has been shipped - tracking: 1Z999AA1234567890

```

**✅ What's new - The `id:` field:**
- **Assigns a unique identifier** to this specific event
- Client **automatically stores** this ID internally as the "last received event ID"
- On reconnection (automatic or manual), client sends: `Last-Event-ID: order-002` header
- **Server responsibility**: Check this header and resume streaming from after `order-002`

**Reconnection Scenario - HTTP Request/Response Flow:**

**Step 1: Initial connection and events received**
```javascript
const eventSource = new EventSource('/mvc/stream/critical-events');

eventSource.addEventListener('trading-alert', function(event) {
    console.log('Trading alert:', event.data); // "AAPL stock hit $150..."
    console.log('Event ID:', event.lastEventId); // "trade-001"
});

eventSource.addEventListener('order-update', function(event) {
    console.log('Order update:', event.data); // "Order #12345 has been shipped..."
    console.log('Event ID:', event.lastEventId); // "order-002"
    // Browser automatically stores "order-002" as last received ID
});
```

**Initial HTTP Request:**
```http
GET /api/critical-events HTTP/1.1
Host: example.com
Accept: text/event-stream
```

**Server Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: trade-001
event: trading-alert
data: AAPL stock hit $150 - trigger sell order


id: order-002
event: order-update
data: Order #12345 has been shipped - tracking: 1Z999AA1234567890

```

**Step 2: Connection drops (network issue, server restart, etc.)**
```javascript
eventSource.onerror = function(error) {
    console.log('Connection lost, browser will auto-reconnect...');
    // Browser has stored "order-002" as the last received ID
};
```

**Step 3: Browser automatically reconnects with Last-Event-ID header**

**Automatic Reconnection HTTP Request:**
```http
GET /api/critical-events HTTP/1.1
Host: example.com
Accept: text/event-stream
Last-Event-ID: order-002
```

**Server Response (resumes from after order-002):**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: payment-003
event: payment-alert
data: Payment of $1,250 processed for Order #12345


id: trade-004
event: trading-alert
data: TSLA stock dropped to $200 - consider buy opportunity

```

**✅ What happened:**
1. **Client received** events `trade-001` and `order-002`
2. **Connection dropped** after `order-002`
3. **Browser automatically reconnected** with `Last-Event-ID: order-002`
4. **Server resumed** from `payment-003` onwards
5. **No critical events lost** - client continues seamlessly

**Why this matters for backend developers:**
- Enables **exactly-once delivery semantics** when implemented correctly
- Server can maintain **event logs/queues** indexed by ID for replay
- Critical for **financial, audit, or mission-critical** event streams
- Your SSE client code needs to persist the last ID across application restarts

### Example 4: Adding Retry Interval

**What if the connection keeps dropping repeatedly?** Consider these scenarios:

**Mobile user on a train:**
- Connection drops every time the train goes through a tunnel
- If browser reconnects immediately, it fails again
- Creates a retry storm that drains battery and wastes bandwidth

**Server restart scenario:**
- Your application server crashed and is restarting
- Takes 30 seconds to come back online
- Browser shouldn't hammer the dead server every few seconds

**How do you control reconnection timing to avoid overwhelming the server or draining mobile batteries?** The `retry:` field lets the server tell clients how long to wait before attempting reconnection.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

retry: 10000
id: train-001
event: location-update
data: Train approaching tunnel - signal may drop


retry: 30000
id: maint-002
event: maintenance-alert
data: Server restart in 30 seconds - will reconnect automatically

```

**✅ What's new:**
- **`retry:` field** sets reconnection delay in milliseconds
- Client updates its internal retry interval
- Affects **all future reconnection attempts** until a new retry value is sent
- Helps control server load during network issues

**Browser consumption:**
```javascript
const eventSource = new EventSource('/mvc/stream/mobile-friendly');

eventSource.addEventListener('location-update', function(event) {
    console.log('Location:', event.data); // "Train approaching tunnel..."
    // Browser now waits 10 seconds before reconnecting on connection loss
});

eventSource.addEventListener('maintenance-alert', function(event) {
    console.log('Maintenance:', event.data); // "Server restart in 30 seconds..."
    // Browser now waits 30 seconds before reconnecting on connection loss
});

eventSource.onerror = function(error) {
    console.log('Connection lost');
    // Browser will wait the last specified retry interval before reconnecting
    // First event set it to 10s, second event updated it to 30s
};
```

**What happens during reconnection:**
1. **Connection drops** (tunnel, server restart, etc.)
2. **Browser waits** 30 seconds (last retry value received)
3. **Reconnects after delay** with `Last-Event-ID: maint-002`
4. **Avoids retry storms** that waste resources
5. **Server has time to recover** from restarts or high load

**Best Practices:**

**When to include retry:**
- **Always include on the first event** - Sets a reasonable default (e.g., 5-10 seconds)
- **Include when conditions change** - Longer delays during maintenance, shorter for normal operations
- **Don't include on every event** - Only when you need to change the interval

**Recommended intervals:**
- **Normal operations**: 5-10 seconds (balances responsiveness with server load)
- **High load periods**: 15-30 seconds (gives server breathing room)
- **Maintenance windows**: 30-60 seconds (avoids hammering during restarts)
- **Mobile/poor connectivity**: 10-15 seconds (conserves battery, accounts for signal issues)

**Implementation pattern:**
```http
# First event - establish baseline
retry: 5000
id: init-001
data: Connection established

# Change only when needed
retry: 30000
id: maint-100
event: maintenance-start
data: Entering maintenance mode

# Return to normal
retry: 5000
id: maint-101
event: maintenance-end
data: Maintenance complete - normal operations resumed
```

### Example 5: Adding Comments

**What if proxies or load balancers drop your SSE connection due to inactivity?** Consider these scenarios:

**Long periods without real events:**
- Stock market closed overnight - no trading alerts for 12 hours
- Monitoring system during quiet periods - no errors to report
- Chat application when users aren't actively messaging

**Corporate network infrastructure:**
- Proxy servers timeout "idle" connections after 60 seconds
- Load balancers drop connections with no traffic
- Firewalls close connections that appear inactive

**How do you keep the connection alive without sending fake events that trigger client-side processing?** Comments (`: lines`) are ignored by the client but keep the HTTP connection active, preventing infrastructure timeouts.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

: Connection established at 2023-12-07T10:30:00Z
retry: 5000
id: init-001
event: system-status
data: System online - monitoring started


: keepalive ping - 10:31:00
: keepalive ping - 10:32:00
: keepalive ping - 10:33:00


id: alert-002
event: error-alert
data: Database connection timeout detected

```

**✅ What's new:**
- **`: comment`** lines are completely ignored by the client
- Useful for **keepalive** - prevents proxy/load balancer timeouts
- Can include documentation, debugging info, or timestamps
- Many servers send periodic comments (every 30-60 seconds) to maintain connections

**Browser consumption:**
```javascript
const eventSource = new EventSource('/mvc/stream/with-keepalive');

eventSource.addEventListener('system-status', function(event) {
    console.log('System status:', event.data); // "System online - monitoring started"
    // Comments are completely invisible to client code
});

eventSource.addEventListener('error-alert', function(event) {
    console.log('Error alert:', event.data); // "Database connection timeout detected"
    // Client never saw the keepalive comments in between
});

// No event is triggered by comment lines - they're invisible to JavaScript
```

**What happens with comments:**
1. **Server sends comments** periodically (e.g., every 60 seconds)
2. **Proxies see HTTP traffic** - connection appears active
3. **Client ignores comments** - no JavaScript events triggered
4. **Connection stays alive** through quiet periods
5. **Real events work normally** when they occur

**Common keepalive patterns:**
```http
# Timestamp-based keepalive
: keepalive 2023-12-07T10:30:00Z

# Simple heartbeat
: ping

# Debugging information
: clients_connected=42 memory_usage=85%

# Documentation/spec reference
: SSE standard: https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events
```

### Example 6: Multiple Data Lines

**What if the event data is multiline?** Consider these common scenarios:

**Formatted JSON object:**
```json
{
  "user": "john",
  "message": "Database operation completed",
  "timestamp": "2023-12-07T10:30:00Z",
  "details": {
    "affected_rows": 42,
    "execution_time": "150ms"
  }
}
```

**Stack trace from an error:**
```
ERROR: Database connection failed
    at DatabaseService.connect(DatabaseService.java:42)
    at UserService.authenticate(UserService.java:18)
    at AuthController.login(AuthController.java:65)
```

**What do we do to ship this to the client?** Do you need to escape newlines? Strip formatting? Calculate content lengths? **No** - SSE handles this elegantly with multiple `data:` lines.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: success-001
event: operation-complete
data: {
data:   "user": "john",
data:   "message": "Database operation completed",
data:   "timestamp": "2023-12-07T10:30:00Z",
data:   "details": {
data:     "affected_rows": 42,
data:     "execution_time": "150ms"
data:   }
data: }


id: error-001
event: error
data: ERROR: Database connection failed
data:     at DatabaseService.connect(DatabaseService.java:42)
data:     at UserService.authenticate(UserService.java:18)
data:     at AuthController.login(AuthController.java:65)

```

**✅ What happens:**
- **Single newlines** between `data:` lines = same event (lines get concatenated)
- **Double newlines** (`\n\n`) = event boundary (marks end of event)
- **Event IDs and types** clearly show where each event begins and ends
- Client receives two separate events with properly formatted multiline content

**Implementation note**: The SSE specification removes **trailing newlines** from the final result, so `data: hello\n` becomes just `"hello"`.

---

## Consuming SSE Streams

### Browser EventSource API

SSE was originally designed for browsers, which provide a simple, high-level API for consuming SSE streams. The browser's `EventSource` API handles most of the complexity automatically:

```javascript
const eventSource = new EventSource('/api/events');

// High-level event handling
eventSource.onmessage = function(event) {
    console.log('Data:', event.data);
    console.log('Last ID:', event.lastEventId);
};

eventSource.addEventListener('custom-event', function(event) {
    console.log('Custom event:', event.data);
});

// Automatic error handling and reconnection
eventSource.onerror = function(error) {
    console.log('Connection error - browser will auto-reconnect');
};
```

**What the browser handles automatically:**
- **HTTP connection management** - No need to use fetch() or manage requests
- **SSE parsing** - Automatically parses `data:`, `event:`, `id:`, `retry:` fields
- **Event dispatching** - Routes events to appropriate handlers based on event type
- **Reconnection logic** - Automatically reconnects with exponential backoff
- **Last-Event-ID handling** - Stores and sends `Last-Event-ID` header on reconnection
- **Cross-origin support** - Handles CORS automatically when configured

### Server-Side SSE Consumption

When consuming SSE streams from server applications (microservices, backend integrations, monitoring systems), you must implement SSE client logic that browsers handle automatically. Key challenges include:

**Protocol Implementation:**
- **HTTP streaming** - maintain persistent connections and parse responses line-by-line
- **Event reconstruction** - accumulate multiple `data:` lines and detect event boundaries
- **Field parsing** - handle `data:`, `event:`, `id:`, `retry:`, and comment lines

**Reliability Requirements:**
- **Reconnection logic** - automatic reconnection with exponential backoff
- **Event ID persistence** - track and persist last processed event across restarts
- **Error handling** - distinguish retryable vs permanent failures

**Production Concerns:**
- **Authentication** - token management and renewal
- **Resource management** - prevent connection and memory leaks
- **Monitoring** - integration with metrics and alerting systems

Unlike browser EventSource which handles these automatically, server-side clients require careful implementation or specialized libraries.

### JVM SSE Client Libraries

#### Built-in JDK Support (Java 21)

**Java HttpClient (java.net.http)**
- **Protocol Support**: HTTP streaming responses, manual SSE parsing required
- **What's Included**: Connection management, HTTP/2 support, reactive streams integration
- **What's Missing**: SSE protocol parsing, event reconstruction, automatic reconnection
- **Implementation Required**: Complete SSE parsing logic, event ID tracking, retry mechanisms

The JDK provides excellent HTTP streaming capabilities but requires significant custom implementation for full SSE compliance.

#### Most Developer-Friendly Option

**OkHttp EventSource**
- **Protocol Support**: Complete SSE specification compliance
- **Developer Experience**: Simple API, automatic reconnection, built-in event parsing
- **Key Features**:
  - Handles all SSE field parsing (`data:`, `event:`, `id:`, `retry:`)
  - Automatic reconnection with exponential backoff
  - Event ID tracking and `Last-Event-ID` header management
  - Configurable connection timeouts and retry policies
- **What's Missing**: Event ID persistence across application restarts (requires custom storage)
- **Best For**: Most production SSE client implementations

OkHttp EventSource provides the most complete, out-of-the-box SSE client experience with minimal boilerplate code.

#### Spring Framework Support

**WebFlux WebClient**
- **Protocol Support**: HTTP streaming, manual SSE parsing required
- **Spring Integration**: Reactive streams, configuration management, metrics integration
- **What's Included**: Streaming response handling, backpressure support, Spring Boot auto-configuration
- **What's Missing**: SSE protocol parsing, event reconstruction, automatic reconnection
- **Implementation Required**: Custom SSE parsing logic, reconnection strategy, event ID management

**Spring's Approach**: Spring focuses on providing excellent HTTP streaming primitives within the reactive ecosystem, expecting developers to implement SSE-specific logic as needed.

**Key Takeaway**: For complete SSE client functionality, most JVM applications benefit from dedicated SSE libraries like OkHttp EventSource rather than building custom implementations on top of general-purpose HTTP clients.

---

## SSE Best Practices

### Key Differences: Browser vs Server Consumption

| Aspect | Browser EventSource | Server-Side Client |
|--------|-------------------|-------------------|
| **Connection** | Automatic HTTP management | Manual HTTP streaming |
| **Parsing** | Built-in SSE parsing | Custom parser required |
| **Reconnection** | Automatic with backoff | Manual implementation |
| **Event ID Storage** | Automatic in memory | Persistent storage needed |
| **Error Handling** | Basic `onerror` callback | Comprehensive error strategies |
| **Resource Management** | Browser-managed | Manual cleanup required |
| **Authentication** | Cookie/session based | Token/header management |

---

## SSE Best Practices

### Event ID Strategy
**Always use meaningful, sequential IDs for reliable event streams:**
- **Sequential format**: `user-001`, `order-002`, `trade-003` (easier debugging)
- **Include entity type**: Helps with event routing and replay logic
- **Avoid UUIDs**: Harder to determine sequence and debug missing events
- **Persist server-side**: Maintain event logs indexed by ID for replay functionality

### Event Type Design
**Design event types for client-side routing and processing:**
- **Use hierarchical naming**: `user.login`, `order.created`, `system.alert`
- **Be specific**: `payment-failed` vs generic `error`
- **Consider client handlers**: Each type should map to a distinct UI action
- **Avoid frequent changes**: Client code needs to handle all event types

### Retry Interval Guidelines
**Set retry intervals based on application context:**
- **Normal operations**: 5-10 seconds (balanced responsiveness)
- **High load periods**: 15-30 seconds (server protection)
- **Maintenance windows**: 30-60 seconds (restart tolerance)
- **Mobile applications**: 10-15 seconds (battery conservation)
- **Update dynamically**: Change retry based on server conditions

### Connection Management
**Design for long-lived, reliable connections:**
- **Implement keepalive**: Send comments every 30-60 seconds
- **Handle reconnection**: Always include `Last-Event-ID` support
- **Graceful degradation**: Provide fallback for SSE-unsupported environments
- **Resource limits**: Set reasonable connection timeouts and limits

### Data Format Consistency
**Maintain consistent event data structures:**
- **Standardize JSON**: Use consistent field names across event types
- **Include metadata**: Timestamp, version, source system in event data
- **Keep payloads focused**: One logical unit per event (don't batch unrelated data)
- **Handle multiline**: Use multiple `data:` lines for formatted content (JSON, logs)

### Security Considerations
**Protect SSE endpoints like any API:**
- **Authentication**: Validate user tokens on connection and during stream
- **Authorization**: Filter events based on user permissions
- **Rate limiting**: Prevent abuse of SSE endpoints
- **HTTPS only**: Never send sensitive data over unencrypted connections

### Error Handling
**Plan for various failure scenarios:**
- **Client disconnection**: Clean up server resources promptly
- **Server restart**: Implement event replay from Last-Event-ID
- **Network issues**: Use appropriate retry intervals
- **Data corruption**: Include event checksums for critical systems

---

## Field Summary

| Field | Purpose | Example | When to Use |
|-------|---------|---------|-------------|
| `data:` | Event payload (can repeat for multi-line) | `data: Hello World` | Every event - contains the actual message content |
| `event:` | Custom event type name | `event: user-login` | When you need different client handlers (notifications vs alerts) |
| `id:` | Event ID for stateful reconnection (client sends `Last-Event-ID` header) | `id: msg-123` | Critical systems where no events can be lost (trading, orders) |
| `retry:` | Client reconnection interval (milliseconds) | `retry: 5000` | Mobile apps, server maintenance, high-load periods |
| `:` | Comment line (ignored by client) | `: keepalive ping` | Long quiet periods, proxy timeouts, debugging info |

---

## Key Benefits

- **Simple**: Uses standard HTTP - no special protocols
- **Firewall-friendly**: Works through proxies and firewalls
- **Auto-reconnection**: Built-in reconnection with resumption
- **Efficient**: Single connection for multiple events
- **Standardized**: Native browser support via EventSource API

SSE is perfect when you need **server-to-client real-time communication** without the bidirectional complexity of WebSockets.
