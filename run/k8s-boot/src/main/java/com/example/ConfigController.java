package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

  @Value("${message:not set}")
  private String message;

  @Value("${test:not set}")
  private String test;

  @GetMapping("/config")
  Map<String, String> get() {
    return Map.of("message", message, "test", test);
  }

  @GetMapping("/config/volume")
  List<Path> getVolume() throws IOException {
    try (Stream<Path> paths = Files.walk(Paths.get("/myconfigs"))) {
      var list = paths.filter(Files::isRegularFile).collect(Collectors.toList());
      return list;
    }
  }
}
