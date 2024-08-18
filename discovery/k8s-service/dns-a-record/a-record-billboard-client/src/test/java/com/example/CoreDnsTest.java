package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.discovery.DnsService;
import com.github.dockerjava.api.model.ExposedPort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.UnknownHostException;
import java.util.List;

@SpringBootTest
public class CoreDnsTest {

  private static CoreDnsContainer corednsContainer;
  private DnsService dnsService;

  @BeforeAll
  public static void setUp() {
    corednsContainer = new CoreDnsContainer("coredns/coredns:1.11.1");
    corednsContainer.start();
  }

  @BeforeEach
  public void init() throws UnknownHostException {
    String dnsServerIp = corednsContainer.getHost();
    int dnsServerPort = corednsContainer.getMappedPort(ExposedPort.tcp(53));
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

