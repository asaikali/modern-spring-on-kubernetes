package com.example.number.mvc;

import java.io.IOException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public abstract class ResponseEmitterBasedSseStream {
    
    private static final Logger log = LoggerFactory.getLogger(ResponseEmitterBasedSseStream.class);
    protected final ResponseBodyEmitter emitter = new ResponseBodyEmitter(0L);
    protected volatile boolean running = true;

    public ResponseBodyEmitter start() {
        // Set up lifecycle handlers
        emitter.onCompletion(() -> {
            running = false;
            onStreamComplete();
        });
        
        emitter.onTimeout(() -> {
            running = false;
            onStreamTimeout();
        });
        
        emitter.onError(throwable -> {
            running = false;
            onStreamError(throwable);
        });

        // Start streaming in virtual thread
        Executors.newVirtualThreadPerTaskExecutor().submit(this::safeRun);
        
        return emitter;
    }

    private void safeRun() {
        try {
            publishEvents();
        } catch (Exception ex) {
            log.error("Error in SSE stream", ex);
        } finally {
            try {
                emitter.complete();
            } catch (Exception ex) {
                log.debug("Error completing emitter: {}", ex.toString());
            }
        }
    }

    // Abstract method for subclasses to implement
    protected abstract void publishEvents() throws IOException, InterruptedException;

    // Helper method to send SSE events
    protected void sendSseEvent(String eventId, String eventType, String data) throws IOException {
        String sseEvent = String.format("id: %s\nevent: %s\ndata: %s\n\n", 
            eventId, eventType, data);
        emitter.send(sseEvent, MediaType.TEXT_PLAIN);
    }

    // Lifecycle hooks for subclasses
    protected void onStreamComplete() {
        log.info("SSE stream completed");
    }
    
    protected void onStreamTimeout() {
        log.info("SSE stream timed out");
    }
    
    protected void onStreamError(Throwable throwable) {
        log.info("SSE stream error: {}", throwable.toString());
    }

    // Method to stop streaming
    public void stop() {
        running = false;
    }
}
