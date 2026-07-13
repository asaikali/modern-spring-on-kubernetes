package com.example.discovery;

import java.util.List;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

public class DemoDiscoveryClient implements DiscoveryClient {

  @Override
  public String description() {
    return "Demo Discovery Client";
  }

  @Override
  public List<ServiceInstance> getInstances(String serviceId) {
    if ("foo".equals(serviceId)) {
      var instance =
          new DefaultServiceInstance(
              serviceId + "-1", serviceId, "jsonplaceholder.typicode.com", 443, true);
      return List.of(instance);
    }
    return List.of();
  }

  @Override
  public List<String> getServices() {
    return List.of("foo");
  }
}
