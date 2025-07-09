package com.example.servlet.async;

import jakarta.servlet.AsyncContext;
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

// @WebServlet(urlPatterns = "/servlet/async", asyncSupported = true)
public class SseServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String countParam = req.getParameter("count");
    // Parse 'count' parameter
    final int count; // default
    if (countParam == null) {
      count = 5;
    } else {
      count = Integer.parseInt(countParam);
    }

    // Set SSE headers
    resp.setContentType("text/event-stream");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Cache-Control", "no-cache");
    resp.setHeader("Connection", "keep-alive");

    AsyncContext asyncContext = req.startAsync();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);
    AtomicInteger current = new AtomicInteger(1);

    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            PrintWriter writer = resp.getWriter();
            int i = current.getAndIncrement();

            writer.write("id: " + i + "\n");
            writer.write("event: message\n");
            writer.write("data: Generated on thread" + Thread.currentThread().getName() + "\n");
            writer.write("data: Event number " + i + "\n\n");
            writer.flush();

            if (i >= count) {
              asyncContext.complete();
              scheduler.shutdown();
            }
          } catch (Exception e) {
            e.printStackTrace();
            asyncContext.complete();
            scheduler.shutdown();
          }
        },
        1,
        1,
        TimeUnit.SECONDS); // initial delay 3s, then every 1s
  }
}
