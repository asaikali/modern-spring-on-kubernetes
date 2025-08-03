package com.example.config.storage;

import com.example.config.storage.pojos.ProviderSettings;
import com.example.config.storage.pojos.StorageProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class StoragePropertiesValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return StorageProperties.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    StorageProperties props = (StorageProperties) target;

    ProviderSettings providers = props.getProviders();

    switch (props.getProvider()) {
      case LOCAL -> {
        if (isBlank(providers.getLocal().getBasePath())) {
          errors.rejectValue(
              "providers.local.basePath",
              "storage.local.base-path.required",
              "Base path is required for local storage.");
        }
      }
      case S3 -> {
        if (isBlank(providers.getS3().getAccessKey())) {
          errors.rejectValue(
              "providers.s3.accessKey",
              "storage.s3.access-key.required",
              "Access key is required for S3.");
        }
        if (isBlank(providers.getS3().getSecretKey())) {
          errors.rejectValue(
              "providers.s3.secretKey",
              "storage.s3.secret-key.required",
              "Secret key is required for S3.");
        }
      }
      case GCS -> {
        if (isBlank(providers.getGcs().getCredentialsPath())) {
          errors.rejectValue(
              "providers.gcs.credentialsPath",
              "storage.gcs.credentials-path.required",
              "Credentials path is required for GCS.");
        }
      }
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
