package com.example;

import com.example.config.storage.pojos.StorageProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  private final StorageProperties storageProperties;

  public RootController(StorageProperties storageProperties) {
    this.storageProperties = storageProperties;
  }

  @GetMapping("/")
  StorageProperties getStorageProperties() {

    System.out.println("configured storage type is " + storageProperties.getType());

    return storageProperties;
  }
}
