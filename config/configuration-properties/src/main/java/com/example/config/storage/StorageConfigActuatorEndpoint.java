package com.example.config.storage;

import com.example.config.storage.pojos.StorageProperties;
import java.util.Map;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "storage-config")
@Component
public class StorageConfigActuatorEndpoint {
  private final StorageProperties props;

  public StorageConfigActuatorEndpoint(StorageProperties props) {
    this.props = props;
  }

  // @ReadOperation
  public Map<String, Object> config() {
    return Map.of(
        "provider", props.getProvider(),
        "type", props.getType().name());
  }

  @ReadOperation
  public StorageProperties configAll() {
    return props;
  }
}
