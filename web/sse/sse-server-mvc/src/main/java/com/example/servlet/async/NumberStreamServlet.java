package com.example.servlet.async;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//@WebServlet(urlPatterns = "/numbers/servlet", asyncSupported = true)
public class NumberStreamServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(NumberStreamServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("NumberStreamServlet initialized");
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        log.info("Received SSE stream request");

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Connection", "keep-alive");

        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(0); // No timeout for long-lived connection

        Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
            try (ServletOutputStream out = asyncContext.getResponse().getOutputStream()) {
                int counter = 0;
                while (true) {
                    String event = formatEvent(counter);
                    out.write(event.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                    counter++;
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (ClientAbortException cae) {
                log.info("Client disconnected: {}", cae.getMessage());
            } catch (IOException ioe) {
                log.warn("IOException while writing to stream", ioe);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while streaming", ie);
            } finally {
                asyncContext.complete();
                log.info("Async context completed");
            }
        });
    }

    private String formatEvent(int id) {
        return String.format("""
                id: %d
                event: number
                data: %d

                """, id, id);
    }
}
