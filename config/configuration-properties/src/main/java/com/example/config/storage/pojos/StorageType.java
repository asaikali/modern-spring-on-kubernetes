package com.example.config.storage.pojos;

public enum StorageType {
  LOCAL("local"),
  S3("s3"),
  GCS("gcs");

  private final String value;

  StorageType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static StorageType fromValue(String value) {
    for (StorageType type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown storage type: " + value);
  }
}
