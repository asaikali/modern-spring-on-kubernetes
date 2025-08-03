# 🧩 Spring Boot Configuration Properties Demo

This project demonstrates **advanced usage of `@ConfigurationProperties` in Spring Boot**, with a
real-world example of configuring a multi-provider file storage service. It is part of the `config`
workspace and illustrates how to bind, validate, and inspect custom configuration structures.

## Features Demonstrated

### Strongly Typed Configuration with Validation

- `@ConfigurationProperties` to bind `storage.*` settings from `application.yaml`
- Type-safe fields including:
    - `String`, `int`, `boolean`, `float`, `LocalDate`
    - `List<String>`, `Map<String, String>`
    - Nested objects
    - Enums with custom values

### Conditional Validation Based on Provider

- A custom `StoragePropertiesValidator` checks that required fields are present **based on the
  selected provider**:
    - `LOCAL` requires `basePath`
    - `S3` requires `accessKey`, `secretKey`, `region`, `bucketName`
    - `GCS` requires `credentialsPath`, `bucketName`

Validation fails on startup if a required field is missing.

### Enum Conversion with Custom String Mapping

- `StorageType` enum uses `@Component` converter to support YAML values like `"s3"`, `"gcs"`, etc.
- Ensures configuration is stable and human-readable.

### Default Values and IDE Autocomplete

- Default values are assigned in field declarations (e.g., `maxRetries = 2`)
- JavaDoc on fields is extracted into `spring-configuration-metadata.json` for use by IDEs
- `spring-boot-configuration-processor` is included to enable metadata generation

### Actuator Integration

- Custom actuator endpoint `/actuator/storage-config` shows the resolved `StorageProperties` object
- Full actuator exposure enabled for demonstration purposes (not production-safe)

---

## Example Configuration (`application.yaml`)

```yaml
storage:
  type: S3
  caching-enabled: true
  max-retries: 5
  throttle-factor: 0.75
  url-expiry: 2025-12-31
  supported-types:
    - image/png
    - image/jpeg
    - application/pdf
  metadata:
    project: "my-app"
    environment: "prod"
  providers:
    local:
      base-path: "/data/files"
    s3:
      access-key: "AKIAEXAMPLE"
      secret-key: "SECRETEXAMPLE"
      region: "us-east-1"
      bucket-name: "my-bucket"
    gcs:
      credentials-path: "/etc/gcp/creds.json"
      bucket-name: "gcs-bucket"
  provider: s3
