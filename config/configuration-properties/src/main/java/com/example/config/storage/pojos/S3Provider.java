package com.example.config.storage.pojos;

import jakarta.validation.constraints.NotBlank;

public class S3Provider {
  @NotBlank private String accessKey;

  @NotBlank private String secretKey;

  @NotBlank private String region;

  @NotBlank private String bucketName;

  public String getAccessKey() {
    return accessKey;
  }

  public S3Provider setAccessKey(String accessKey) {
    this.accessKey = accessKey;
    return this;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public S3Provider setSecretKey(String secretKey) {
    this.secretKey = secretKey;
    return this;
  }

  public String getRegion() {
    return region;
  }

  public S3Provider setRegion(String region) {
    this.region = region;
    return this;
  }

  public String getBucketName() {
    return bucketName;
  }

  public S3Provider setBucketName(String bucketName) {
    this.bucketName = bucketName;
    return this;
  }
}
