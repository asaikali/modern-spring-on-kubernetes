package com.example.numbers;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.Duration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SseStreamController {

    @GetMapping("/sse/numbers")
    public void streamNumbers(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync();

        new AbstractSseStream(asyncContext) {
            @Override
            protected void publishEvents(PrintWriter writer) {
                int counter = 0;
                while (true) {
                    String event = """
                    id: %d
                    event: number
                    data: %d

                    """.formatted(counter, counter);
                    sendEvent(writer, event);
                    pause(Duration.ofSeconds(1));
                    counter++;
                }
            }
        }.start();
    }
}
