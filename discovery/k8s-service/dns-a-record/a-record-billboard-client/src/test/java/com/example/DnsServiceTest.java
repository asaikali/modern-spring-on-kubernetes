package com.example;

import com.example.discovery.DnsService;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.com.google.common.base.Preconditions;
import org.testcontainers.utility.DockerImageName;

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DnsServiceTest {

  private static final int DNS_PORT = 2554;
  private static GenericContainer<?> corednsContainer;
  private DnsService dnsService;

  @BeforeAll
  public static void setUp() {
    corednsContainer = new GenericContainer<>(DockerImageName.parse("coredns/coredns:1.10.1"))
        .withClasspathResourceMapping("Corefile", "/Corefile", BindMode.READ_ONLY)
        .withClasspathResourceMapping("db.example.test", "/db.example.test", BindMode.READ_ONLY)
        .withCommand("-conf", "/Corefile")
        .withCreateContainerCmdModifier(cmd -> {
          ExposedPort exposedPort = ExposedPort.udp(DNS_PORT);
          cmd.withExposedPorts(exposedPort);

          PortBinding portBinding = new PortBinding(Ports.Binding.empty(), exposedPort);
          cmd.getHostConfig().withPortBindings(portBinding);
        });


    corednsContainer.start();
  }

  @BeforeEach
  public void init() throws UnknownHostException {
    String dnsServerIp = corednsContainer.getHost();
    int dnsServerPort = getMappedPort(corednsContainer, DNS_PORT, InternetProtocol.UDP);
    dnsService = new DnsService(dnsServerIp, dnsServerPort);
  }

  @AfterAll
  public static void tearDown() {
    corednsContainer.stop();
  }

  @Test
  public void testResolveARecord() throws UnknownHostException {
    List<String> ips = dnsService.resolveARecord("example.test");
    assertEquals(1, ips.size());
    assertEquals("192.168.1.1", ips.get(0));
  }

  @Test
  public void testResolveARecordInvalidDomain() {
    var ips = dnsService.resolveARecord("invalid-domain.test");
    assertEquals(0, ips.size());
  }

  private Integer getMappedPort(GenericContainer<?> container, int originalPort, InternetProtocol protocol) {
    Preconditions.checkState(container.getContainerId() != null, "Mapped port can only be obtained after the container is started");
    Ports.Binding[] binding = new Ports.Binding[0];
    InspectContainerResponse containerInfo = container.getContainerInfo();
    if (containerInfo != null) {
      binding = (Ports.Binding[])containerInfo.getNetworkSettings().getPorts().getBindings().get(new ExposedPort(originalPort, protocol));
    }

    if (binding != null && binding.length > 0 && binding[0] != null) {
      return Integer.valueOf(binding[0].getHostPortSpec());
    } else {
      throw new IllegalArgumentException("Requested port (" + originalPort + ") is not mapped");
    }
  }
}
