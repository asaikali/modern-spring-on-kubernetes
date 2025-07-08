package com.example.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class MvcStreamController {

    private final TaskExecutor taskExecutor;

    @Autowired
    public MvcStreamController(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Endpoint: /mvc/stream/one
     * Sends a single SSE event including all standard fields and a comment with the MDN spec link.
     */
    @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOneFullSpecEvent() {
        SseEmitter emitter = new SseEmitter();

        taskExecutor.execute(() -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()

                        // SSE comment line with concise text and MDN link explaining standard fields
                        .comment("SSE standard fields: https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#event_stream_format")

                        // Event ID used for Last-Event-ID header resumption
                        .id("event-1")

                        // Event type name
                        .name("event-type")

                        // Event payload data
                        .data("This is the event data")

                        // Reconnect delay in milliseconds
                        .reconnectTime(5000L);

                emitter.send(event);
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }

    // The infinite stream endpoint can be added here later
}
