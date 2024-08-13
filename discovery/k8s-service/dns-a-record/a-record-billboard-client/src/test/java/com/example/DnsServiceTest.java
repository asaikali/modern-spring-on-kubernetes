package com.example;

import com.example.discovery.DnsService;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.Ports;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
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
        .withExposedPorts(DNS_PORT)
        .withClasspathResourceMapping("Corefile", "/Corefile", BindMode.READ_ONLY)
        .withClasspathResourceMapping("db.example.test", "/db.example.test", BindMode.READ_ONLY)
        .withCommand("-conf", "/Corefile","-p", "2554:2554/udp");


    corednsContainer.start();
  }

  @BeforeEach
  public void init() throws UnknownHostException {
    String dnsServerIp = corednsContainer.getHost();
    int dnsServerPort = corednsContainer.getMappedPort(DNS_PORT);
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
}
