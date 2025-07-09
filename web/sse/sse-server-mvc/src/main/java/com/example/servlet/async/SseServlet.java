package com.example.servlet.async;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asynchronous Servlet Processing Demo
 *
 * <p>This servlet demonstrates asynchronous request processing using the Servlet 3.0+ API. The
 * async processing pattern allows servlets to handle long-running operations without blocking
 * precious servlet container threads.
 *
 * <p>Key concepts demonstrated: - Async servlet processing (frees up servlet threads) - Background
 * task delegation to separate thread pools - Proper async lifecycle management and cleanup - Error
 * handling and timeout management in async context
 *
 * <p>Async Processing Lifecycle: 1. Client sends HTTP request → Container assigns servlet thread 2.
 * startAsync() marks request as async → Servlet thread becomes available for other requests 3.
 * doGet() method returns immediately → Original servlet thread is released back to pool 4.
 * Background processing continues on scheduler threads → Long-running work doesn't block servlet
 * threads 5. asyncContext.complete() signals completion → Response is finalized and sent to client
 * 6. Connection is closed and resources are cleaned up
 *
 * <p>Benefits of async processing: - Scalability: Servlet threads aren't blocked during
 * long-running operations - Better resource utilization: Thread pool efficiency is maximized -
 * Improved throughput: Container can handle more concurrent requests - Non-blocking I/O: Background
 * tasks can perform time-consuming operations
 */
// @WebServlet(urlPatterns = "/servlet/async", asyncSupported = true)
// Note: asyncSupported = true is REQUIRED for async processing to work
public class SseServlet extends HttpServlet {
  private final Logger logger = LoggerFactory.getLogger(SseServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    // ========== PARAMETER PARSING ==========
    // Extract the number of background iterations from query parameter
    // Example: /servlet/async?count=10 will perform 10 background iterations
    String countParam = req.getParameter("count");
    final int count;
    if (countParam == null) {
      count = 5;
    } else {
      count = Integer.parseInt(countParam);
    }

    // ========== RESPONSE CONFIGURATION ==========
    // Configure response headers for streaming output
    // Note: These headers are used for the demo but the focus is on async processing
    resp.setContentType("text/event-stream");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Cache-Control", "no-cache");
    resp.setHeader("Connection", "keep-alive");

    // ========== ASYNC CONTEXT SETUP ==========
    // Start asynchronous processing - this is the key to non-blocking servlets
    AsyncContext asyncContext = req.startAsync();

    // Set timeout for async operation (10 seconds)
    // If processing takes longer than this, onTimeout() will be called
    // if the app does not call asyncContext.complete() in less than 10 seconds it will
    // per the spec https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1
    //  If the time out is not specified via the call to setTimeout, 30000 is used as the default. A
    // value of 0 or less indicates that the asynchronous operation will never time out.
    asyncContext.setTimeout(10000);

    // ========== ASYNC LIFECYCLE LISTENERS ==========
    // Register listeners to handle various async processing events
    asyncContext.addListener(
        new AsyncListener() {

          @Override
          public void onComplete(AsyncEvent event) throws IOException {
            // Called when async processing completes successfully
            // This is where you'd typically clean up resources
            logger.info("Async processing complete.");
          }

          @Override
          public void onTimeout(AsyncEvent event) throws IOException {
            // Called when async processing exceeds the timeout period
            // Important: We must handle this gracefully and close the connection
            logger.info("Async processing timed out.");

            // Send error message to client before closing
            HttpServletResponse response =
                (HttpServletResponse) event.getAsyncContext().getResponse();
            response.getWriter().write("event: error\ndata: Timeout occurred\n\n");
            response.getWriter().flush();

            // Complete the async context to close the connection
            event.getAsyncContext().complete();
          }

          @Override
          public void onError(AsyncEvent event) throws IOException {
            // Called when an error occurs during async processing
            // Log the error for debugging purposes
            logger.info("Async processing error: " + event.getThrowable());
          }

          @Override
          public void onStartAsync(AsyncEvent event) throws IOException {
            // Called when async processing is restarted (rare in typical usage)
            logger.info("Async cycle restarted.");
          }
        });

    // ========== BACKGROUND TASK SCHEDULER ==========
    // Create a thread pool for background processing - this is the key to async servlets
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);

    // Thread-safe counter for tracking iteration numbers
    AtomicInteger current = new AtomicInteger(1);

    // ========== BACKGROUND TASK FOR ASYNC PROCESSING ==========
    // Schedule a recurring task to simulate long-running background work
    // This demonstrates how async servlets can delegate work to background threads
    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            PrintWriter writer = resp.getWriter();
            int i = current.getAndIncrement();

            // ========== RESPONSE DATA GENERATION ==========
            // Generate response data in event-stream format for demo purposes
            // The key point is that this work happens on a background thread
            writer.write("id: " + i + "\n");
            writer.write("event: message\n");
            writer.write("data: Generated on thread " + Thread.currentThread().getName() + "\n");
            writer.write("data: Event number " + i + "\n\n");

            // Flush immediately to send data to client
            writer.flush();

            // ========== COMPLETION LOGIC ==========
            // Check if we've completed all iterations
            if (i >= count) {
              // Signal that async processing is complete
              asyncContext.complete();

              // Shut down scheduler to free resources
              scheduler.shutdown();
            }
          } catch (Exception e) {
            e.printStackTrace();

            // Clean up resources on error - critical in async processing
            asyncContext.complete();
            scheduler.shutdown();
          }
        },
        1, // Initial delay: wait 1 second before first iteration
        1, // Period: process every 1 second
        TimeUnit.SECONDS // Time unit for delays
        );

    // ========== METHOD COMPLETION ==========
    // Important: doGet() returns immediately after starting async processing
    // The original servlet thread is now free to handle other requests
    // Background scheduler continues processing until asyncContext.complete() is called
    // This is the core benefit of async servlets - non-blocking request handling
  }
}
