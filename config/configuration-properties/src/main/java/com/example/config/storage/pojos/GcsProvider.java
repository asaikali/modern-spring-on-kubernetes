package com.example.config.storage.pojos;

import jakarta.validation.constraints.NotBlank;

public class GcsProvider {
  @NotBlank private String credentialsPath;

  @NotBlank private String bucketName;

  public String getCredentialsPath() {
    return credentialsPath;
  }

  public GcsProvider setCredentialsPath(String credentialsPath) {
    this.credentialsPath = credentialsPath;
    return this;
  }

  public String getBucketName() {
    return bucketName;
  }

  public GcsProvider setBucketName(String bucketName) {
    this.bucketName = bucketName;
    return this;
  }
}
