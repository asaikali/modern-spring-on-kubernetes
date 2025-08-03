package com.example;

import com.example.config.storage.pojos.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ConfigurationPropertiesApplication {

  //  @Bean
  //  public Validator configurationPropertiesValidator() {
  //    return new StoragePropertiesValidator();
  //  }

  public static void main(String[] args) {
    SpringApplication.run(ConfigurationPropertiesApplication.class, args);
  }
}
