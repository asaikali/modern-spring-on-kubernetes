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
GET /mvc/stream/minimal HTTP/1.1
Host: localhost:8080
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

### Example 2: Multiple Data Lines

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

data: {
data:   "user": "john",
data:   "message": "Database operation completed",
data:   "timestamp": "2023-12-07T10:30:00Z",
data:   "details": {
data:     "affected_rows": 42,
data:     "execution_time": "150ms"
data:   }
data: }


event: error
data: ERROR: Database connection failed
data:     at DatabaseService.connect(DatabaseService.java:42)
data:     at UserService.authenticate(UserService.java:18)
data:     at AuthController.login(AuthController.java:65)

```

**✅ What happens:**
- **Single newlines** between `data:` lines = same event (lines get concatenated)
- **Double newlines** (`\n\n`) = event boundary (marks end of event)
- Client receives two separate events with properly formatted multiline content

**Implementation note**: The SSE specification removes **trailing newlines** from the final result, so `data: hello\n` becomes just `"hello"`.

### Example 3: Adding Event Type

By default, all events trigger the client's `message` event. The `event:` field lets you specify custom event types.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

event: notification
data: You have a new message

```

**✅ What's new:**
- **`event:` field** sets a custom event type
- Instead of `onmessage`, this triggers the client's `notification` event listener
- JavaScript: `eventSource.addEventListener('notification', handler)`

### Example 4: Adding Event ID

Event IDs enable **stateful reconnection resumption** - a critical feature for reliable event streaming. When a connection drops, the client can tell the server which was the last event it successfully received.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: msg-001
event: notification
data: You have a new message

```

**✅ What's new - The `id:` field:**
- **Assigns a unique identifier** to this specific event
- Client **automatically stores** this ID internally as the "last received event ID"
- On reconnection (automatic or manual), client sends: `Last-Event-ID: msg-001` header
- **Server responsibility**: Check this header and resume streaming from after `msg-001`
- **Client implementation note**: If you're building a custom SSE client, you must:
  - Store the last received `id` value
  - Include it in the `Last-Event-ID` header on reconnection
  - Handle the server's response appropriately (server may send historical events you missed)

**Why this matters for backend developers:**
- Enables **exactly-once delivery semantics** when implemented correctly
- Server can maintain **event logs/queues** indexed by ID for replay
- Critical for **financial, audit, or mission-critical** event streams
- Your SSE client code needs to persist the last ID across application restarts

### Example 5: Adding Retry Interval

The `retry:` field tells the client how long to wait before attempting to reconnect after a connection failure.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

retry: 3000
id: msg-001
event: notification
data: You have a new message

```

**✅ What's new:**
- **`retry:` field** sets reconnection delay to 3000 milliseconds (3 seconds)
- Client updates its internal retry interval
- Affects **all future reconnection attempts** until a new retry value is sent
- Helps control server load during network issues

### Example 6: Adding Comments

Comments are lines starting with `:` that the client ignores. They're useful for keeping connections alive and adding documentation.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

: SSE standard fields: https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events
retry: 3000
id: msg-001
event: notification
data: You have a new message

```

**✅ What's new:**
- **`: comment`** lines are ignored by the client
- Useful for **keepalive** - prevents proxy timeouts
- Can include documentation or debugging information
- Many servers send periodic comments to keep connections active

---

## Complete Example: All Fields Together

Here's how our Spring Boot sample creates an event with all SSE fields:

**Spring Boot Controller:**
```java
@GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamOneFullSpecEvent() {
    SseEmitter emitter = new SseEmitter();
    
    taskExecutor.execute(() -> {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .comment("SSE standard fields: https://developer.mozilla.org/...")
                .id("event-1")
                .name("event-type")
                .data("This is the event data")
                .reconnectTime(5000L);
                
            emitter.send(event);
            emitter.complete();
        } catch (IOException ex) {
            emitter.completeWithError(ex);
        }
    });
    
    return emitter;
}
```

**Resulting HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

: SSE standard fields: https://developer.mozilla.org/...
retry: 5000
id: event-1
event: event-type
data: This is the event data

```

---

## Field Summary

| Field | Purpose | Example |
|-------|---------|---------|
| `data:` | Event payload (can repeat for multi-line) | `data: Hello World` |
| `event:` | Custom event type name | `event: user-login` |
| `id:` | Event ID for stateful reconnection (client sends `Last-Event-ID` header) | `id: msg-123` |
| `retry:` | Client reconnection interval (milliseconds) | `retry: 5000` |
| `:` | Comment line (ignored by client) | `: keepalive ping` |

---

## Client-Side Implementation

### Browser JavaScript (EventSource)
```javascript
const eventSource = new EventSource('/mvc/stream/one');

// Listen for default 'message' events
eventSource.onmessage = function(event) {
    console.log('Received:', event.data);
    console.log('Event ID:', event.lastEventId); // Browser automatically tracks this
};

// Listen for custom event types
eventSource.addEventListener('notification', function(event) {
    console.log('Notification:', event.data);
});

// Handle errors and reconnection
eventSource.onerror = function(error) {
    console.error('SSE Error:', error);
    // Browser automatically reconnects with Last-Event-ID header
};
```

### Custom SSE Client (Backend Integration)
If you're building a server-to-server SSE client or using a custom HTTP client:

```java
// Example: Custom SSE client responsibilities
public class CustomSseClient {
    private String lastEventId = null;
    
    public void connect(String url) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache");
            
        // Include Last-Event-ID if we have one from previous connection
        if (lastEventId != null) {
            requestBuilder.header("Last-Event-ID", lastEventId);
        }
        
        // Handle response stream, parse SSE format, store event IDs...
    }
    
    private void handleSseEvent(String id, String event, String data) {
        if (id != null) {
            this.lastEventId = id; // Store for reconnection
        }
        // Process the event...
    }
}
```

**Key responsibilities for custom clients:**
- Parse SSE format: field names, colons, newlines
- Store `id` values for reconnection via `Last-Event-ID` header
- Handle `retry` field to respect server's reconnection timing
- Implement automatic reconnection logic
- Buffer/queue events during brief disconnections

---

## Key Benefits

- **Simple**: Uses standard HTTP - no special protocols
- **Firewall-friendly**: Works through proxies and firewalls
- **Auto-reconnection**: Built-in reconnection with resumption
- **Efficient**: Single connection for multiple events
- **Standardized**: Native browser support via EventSource API

SSE is perfect when you need **server-to-client real-time communication** without the bidirectional complexity of WebSockets.
