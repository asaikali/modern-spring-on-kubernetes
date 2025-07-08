# Server-Sent Events (SSE) Fundamentals

## Overview

Server-Sent Events (SSE) is a web standard that enables a server to push real-time updates to a web browser over a single HTTP connection. Unlike WebSockets, SSE provides **unidirectional communication** from server to client, making it perfect for scenarios like live notifications, real-time dashboards, chat messages, or system monitoring feeds.

Think of SSE as a persistent HTTP connection where the server can continuously "drip-feed" data to the client as events occur, without the client needing to repeatedly poll for updates.

## Background & History

Server-Sent Events was introduced as part of the **HTML5 specification** around **2009-2010**, standardized by the W3C and WHATWG. The technology emerged during the push for more interactive web applications, addressing the need for real-time communication without the complexity of WebSockets.

**Key historical context:**
- **Before SSE (2000s)**: Developers used polling, long-polling, or complex iframe hacks
- **2009-2010**: SSE standardized as part of HTML5's EventSource API
- **2011+**: Widespread browser adoption (IE was the last to support it in IE11)
- **Today**: SSE is baseline supported across all modern browsers

The standard was designed to be **simple and lightweight** - leveraging existing HTTP infrastructure while providing the real-time capabilities developers needed.

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

SSE allows multiple `data:` lines within a single event. The client automatically joins them with newlines.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

data: This is line one
data: This is line two
data: This is line three

```

**✅ What's new:**
- **Multiple `data:` lines** are concatenated by the client
- Client receives a single event with content:
  ```
  This is line one
  This is line two
  This is line three
  ```

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

Event IDs enable **automatic reconnection resumption**. If the connection drops, the browser sends the last received ID to resume from that point.

**HTTP Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache

id: msg-001
event: notification
data: You have a new message

```

**✅ What's new:**
- **`id:` field** assigns a unique identifier to this event
- On reconnection, browser sends: `Last-Event-ID: msg-001`
- Server can resume sending events after `msg-001`
- Enables **fault-tolerant streaming** - no lost events

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
| `id:` | Event ID for reconnection resumption | `id: msg-123` |
| `retry:` | Client reconnection interval (milliseconds) | `retry: 5000` |
| `:` | Comment line (ignored by client) | `: keepalive ping` |

---

## Client-Side JavaScript

To consume these events in the browser:

```javascript
const eventSource = new EventSource('/mvc/stream/one');

// Listen for default 'message' events
eventSource.onmessage = function(event) {
    console.log('Received:', event.data);
};

// Listen for custom event types
eventSource.addEventListener('notification', function(event) {
    console.log('Notification:', event.data);
});

// Handle errors
eventSource.onerror = function(error) {
    console.error('SSE Error:', error);
};
```

---

## Key Benefits

- **Simple**: Uses standard HTTP - no special protocols
- **Firewall-friendly**: Works through proxies and firewalls
- **Auto-reconnection**: Built-in reconnection with resumption
- **Efficient**: Single connection for multiple events
- **Standardized**: Native browser support via EventSource API

SSE is perfect when you need **server-to-client real-time communication** without the bidirectional complexity of WebSockets.
