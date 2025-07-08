package com.example.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

    @Bean(name = "sseTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);        // minimum number of threads
        executor.setMaxPoolSize(20);         // maximum number of threads
        executor.setQueueCapacity(100);      // queue size before new threads are created up to maxPoolSize
        executor.setThreadNamePrefix("SSE-"); // useful for debugging thread names
        executor.initialize();
        return executor;
    }
}
