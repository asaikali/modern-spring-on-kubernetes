package com.example.config.storage.pojos;

import jakarta.validation.constraints.NotBlank;

public class LocalProvider {
  @NotBlank private String basePath;

  public String getBasePath() {
    return basePath;
  }

  public LocalProvider setBasePath(String basePath) {
    this.basePath = basePath;
    return this;
  }
}
