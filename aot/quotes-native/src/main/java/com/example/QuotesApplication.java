package com.example;

import java.nio.charset.StandardCharsets;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuotesApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuotesApplication.class, args);

    FluentConfiguration configuration =
        new FluentConfiguration()
            .dataSource("jdbc:h2:mem:test", "user", "password")
            .encoding(StandardCharsets.UTF_8)
            .locations("classpath:db/migration");

    if (isRunningInNativeImage()) {
      configuration.resourceProvider(new GraalVMResourceProvider(configuration.getLocations()));
    }

    Flyway flyway = configuration.load();
    MigrateResult result = flyway.migrate();

    System.out.println("Migration successful: " + result.success);
    System.out.println("Executed migrations: " + result.migrationsExecuted);
  }

  private static boolean isRunningInNativeImage() {
    return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
  }
}
