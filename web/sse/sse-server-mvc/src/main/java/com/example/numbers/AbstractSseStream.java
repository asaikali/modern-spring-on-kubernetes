package com.example.numbers;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

public abstract class AbstractSseStream {

    protected final AsyncContext asyncContext;

    public AbstractSseStream(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
        this.asyncContext.setTimeout(0);
    }

    public final void start() {
        Thread.startVirtualThread(() -> {
            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            try {
                prepareResponse(response);
                PrintWriter writer = response.getWriter();
                publishEvents(writer);
            } catch (IOException | RuntimeException e) {
                // optionally log error
            } finally {
                asyncContext.complete();
            }
        });
    }

    /**
     * Override this to customize HTTP headers before writing begins.
     */
    protected void prepareResponse(HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Connection", "keep-alive");
    }

    /**
     * Subclasses implement this to emit one or more SSE events.
     */
    protected abstract void publishEvents(PrintWriter writer);

    /**
     * Sends a raw SSE event block. Caller is responsible for all lines and line endings.
     */
    protected void sendEvent(PrintWriter writer, String sseFormattedBlock) {
        checkInterruption();
        writer.write(sseFormattedBlock);
        writer.flush();
    }

    protected void pause(Duration duration) {
        checkInterruption();
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during pause", e);
        }
    }

    protected void checkInterruption() {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Stream interrupted");
        }
    }
}
