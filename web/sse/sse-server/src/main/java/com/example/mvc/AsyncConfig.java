package com.example.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

  @Bean
  public TaskScheduler sseScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    scheduler.setPoolSize(20);

    // Set thread name prefix for easier debugging
    scheduler.setThreadNamePrefix("sse-scheduler-");

    // Configure graceful shutdown
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(30);

    // Reject tasks when pool is full instead of queuing
    scheduler.setRejectedExecutionHandler(
        new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

    // Initialize the scheduler
    scheduler.initialize();

    return scheduler;
  }
}
