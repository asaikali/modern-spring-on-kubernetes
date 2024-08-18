package com.example.discovery;

import java.util.List;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

@Service
public class DnsDiscoverClient implements DiscoveryClient {

  private final DnsService dnsService;

  public DnsDiscoverClient(DnsService dnsService) {
    this.dnsService = dnsService;
  }

  @Override
  public String description() {
    return "dns discover client";
  }

  @Override
  public List<ServiceInstance> getInstances(String serviceId) {
    var ips = this.dnsService.resolveARecord(serviceId);

    return ips.stream()
        .map(ip -> (ServiceInstance) new DefaultServiceInstance(ip, serviceId, ip, 8080, false))
        .toList();
  }

  @Override
  public List<String> getServices() {
    return List.of();
  }
}
