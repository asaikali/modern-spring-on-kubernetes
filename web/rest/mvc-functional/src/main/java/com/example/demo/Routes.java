package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

  @Bean
  RouterFunction<ServerResponse> helloRoute() {
    return RouterFunctions.route()
        .GET("/hello", req -> ServerResponse.ok().body("Hello World"))
        .build();
  }
}
