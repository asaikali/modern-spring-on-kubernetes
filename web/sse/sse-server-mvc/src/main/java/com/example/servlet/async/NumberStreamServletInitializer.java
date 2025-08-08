package com.example.servlet.async;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

@Component
public class NumberStreamServletInitializer implements ServletContextInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    ServletRegistration.Dynamic registration = servletContext.addServlet(
        "numberStreamServlet", new NumberStreamServlet());

    registration.setAsyncSupported(true);
    registration.addMapping("/servlet/direct");
    registration.setLoadOnStartup(1);
  }
}
