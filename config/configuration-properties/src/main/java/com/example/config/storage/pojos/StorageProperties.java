package com.example.config.storage.pojos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Spring can auto generate metadata that goes into META-INF/spring-configuration-metadata.json
 * which can help IDEs with outo complete. However, you need to add the annotation processor
 *
 * <p><dependency> <groupId>org.springframework.boot</groupId>
 * <artifactId>spring-boot-configuration-processor</artifactId> <optional>true</optional>
 * </dependency>
 */
@Validated
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

  // default values can be set on the initial values in case
  // the application.yaml does not have any values

  /**
   * Adding a javadoc comments will cause it be put into the
   * META-INF/spring-configuration-metadata.json for IDE code assist tools to pick it up.
   */
  private Provider provider;

  @NotNull private StorageType type = StorageType.LOCAL;
  private boolean cachingEnabled;
  private int maxRetries = 2;
  private float throttleFactor;
  private LocalDate urlExpiry;
  private List<String> supportedTypes;
  private Map<String, String> metadata;

  private ProviderSettings providers = new ProviderSettings();

  public Provider getProvider() {
    return provider;
  }

  public StorageProperties setProvider(Provider provider) {
    this.provider = provider;
    return this;
  }

  public StorageType getType() {
    return type;
  }

  public StorageProperties setType(StorageType type) {
    this.type = type;
    return this;
  }

  public boolean isCachingEnabled() {
    return cachingEnabled;
  }

  public StorageProperties setCachingEnabled(boolean cachingEnabled) {
    this.cachingEnabled = cachingEnabled;
    return this;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public StorageProperties setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  public float getThrottleFactor() {
    return throttleFactor;
  }

  public StorageProperties setThrottleFactor(float throttleFactor) {
    this.throttleFactor = throttleFactor;
    return this;
  }

  public LocalDate getUrlExpiry() {
    return urlExpiry;
  }

  public StorageProperties setUrlExpiry(LocalDate urlExpiry) {
    this.urlExpiry = urlExpiry;
    return this;
  }

  public List<String> getSupportedTypes() {
    return supportedTypes;
  }

  public StorageProperties setSupportedTypes(List<String> supportedTypes) {
    this.supportedTypes = supportedTypes;
    return this;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public StorageProperties setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
    return this;
  }

  public ProviderSettings getProviders() {
    return providers;
  }

  public StorageProperties setProviders(ProviderSettings providers) {
    this.providers = providers;
    return this;
  }
}
