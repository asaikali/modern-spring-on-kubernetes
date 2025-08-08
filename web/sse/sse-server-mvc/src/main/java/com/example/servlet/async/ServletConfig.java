package com.example.servlet.async;

import jakarta.servlet.Servlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

  @Bean
  public ServletRegistrationBean<SseServlet> sseServlet() {
    ServletRegistrationBean<SseServlet> bean =
        new ServletRegistrationBean<>(new SseServlet(), "/servlet/async");
    bean.setAsyncSupported(true);
    return bean;
  }

//  @Bean
//  public ServletRegistrationBean<Servlet> numberStreamServlet() {
//    ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>(new NumberStreamServlet(), "/servlet/numbers");
//    bean.setAsyncSupported(true);
//    return bean;
//  }
}
