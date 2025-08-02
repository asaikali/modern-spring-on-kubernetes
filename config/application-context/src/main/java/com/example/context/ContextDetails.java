package com.example.context;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ContextDetails implements CommandLineRunner {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${message:default message}")
  private String message;

  private final ApplicationContext applicationContext;
  private final Environment environment;

  public ContextDetails(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    this.environment = applicationContext.getEnvironment();
  }

  private String bold(String text) {
    return "\033[1m" + text + "\033[0m";
  }

  private void printMessageValue() {
    log.info(bold("source:"));
    log.info(" @Value     : '{}'", message);
    log.info(" environment: '{}'", environment.getProperty("message"));
  }

  private int countContexts(ApplicationContext applicationContext) {
    if (applicationContext == null) return 0;
    return 1 + countContexts(applicationContext.getParent());
  }

  private void contextDetails(ApplicationContext child, ApplicationContext ctx) {
    ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

    log.info("");
    log.info(bold("Application context:"));
    log.info(" id          : {}", ctx.getId());
    log.info(" name        : {}", ctx.getApplicationName());
    log.info(" Parent Id   : {}", ctx.getParent() == null ? "null" : ctx.getParent().getId());
    log.info(" bean count  : {}", ctx.getBeanDefinitionCount());
    log.info(" startup date: {}", Instant.ofEpochMilli(ctx.getStartupDate()));
    log.info(" class       : {}", ctx.getClass().getName());
    log.info(" display name: {}", ctx.getDisplayName());
    log.info("");
    log.info(bold("Environment:"));
    log.info("  class            : {}", environment.getClass().getName());
    log.info("  active profiles  : {}", environment.getActiveProfiles());
    log.info("  default profiles : {}", environment.getDefaultProfiles());
    log.info("  property sources :");

    env.getPropertySources()
        .forEach(
            ps -> {
              log.info("    {} ", ps.toString());
            });

    if (ctx.getParent() != null) {
      log.info("Printing Parent Context Details");
      contextDetails(ctx, ctx.getParent());
    }
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(bold("-- Resolved Property values -- "));
    printMessageValue();
    log.info("");
    log.info(bold("-- Found {} Application Contexts --"), countContexts(applicationContext));
    contextDetails(null, applicationContext);
    log.info("");
  }
}
