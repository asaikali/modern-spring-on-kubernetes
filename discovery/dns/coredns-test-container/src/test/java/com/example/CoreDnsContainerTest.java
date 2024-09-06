package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.coredns.CoreDnsContainer;
import com.example.coredns.CoreFile;
import com.example.coredns.ZoneFile;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.TextParseException;

public class CoreDnsContainerTest {

  private static CoreDnsContainer corednsContainer;
  private static ARecord EAST_WORKER_1, EAST_WORKER_2, EAST_WORKER_3, WEST_WORKER_1, WEST_WORKER_2;

  static {
    try {
      EAST_WORKER_1 =
          new ZoneFile.Builders.ARecord()
              .name("worker.east.example.test")
              .ip("127.0.0.1")
              .ttl(3600)
              .build();

      EAST_WORKER_2 =
          new ZoneFile.Builders.ARecord()
              .name("worker.east.example.test")
              .ip("127.0.0.2")
              .ttl(3600)
              .build();

      EAST_WORKER_3 =
          new ZoneFile.Builders.ARecord()
              .name("worker.east.example.test")
              .ip("127.0.0.3")
              .ttl(3600)
              .build();

      WEST_WORKER_1 =
          new ZoneFile.Builders.ARecord()
              .name("worker.west.example.test")
              .ip("127.0.0.6")
              .ttl(3600)
              .build();

      WEST_WORKER_2 =
          new ZoneFile.Builders.ARecord()
              .name("worker.west.example.test")
              .ip("127.0.0.7")
              .ttl(3600)
              .build();

    } catch (TextParseException e) {
      throw new RuntimeException(e);
    }
  }

  private DnsTestClient dnsTestClient;

  @BeforeAll
  public static void setUp() throws UnknownHostException, TextParseException {
    CoreFile coreFile = new CoreFile();
    ZoneFile zoneFile =
        new ZoneFile(
            "example.test", List.of(EAST_WORKER_1, EAST_WORKER_2, WEST_WORKER_1, WEST_WORKER_2));
    corednsContainer = new CoreDnsContainer(coreFile, zoneFile);
    corednsContainer.start();
  }

  @BeforeEach
  public void init() throws UnknownHostException {
    String dnsServerIp = corednsContainer.getHost();
    int dnsServerPort = corednsContainer.getDnsTcpPort();
    dnsTestClient = new DnsTestClient(dnsServerIp, dnsServerPort);
  }

  @AfterAll
  public static void tearDown() {
    corednsContainer.stop();
  }

  @Test
  @DisplayName("Valid domains resolve")
  public void testResolveARecord() throws UnknownHostException {
    List<String> ips = dnsTestClient.resolveArecords("worker.east.example.test");

    Assertions.assertThat(ips).hasSize(2);
    Assertions.assertThat(ips).contains(ip(EAST_WORKER_1), ip(EAST_WORKER_2));
  }

  @Test
  @DisplayName("Updates to Zonefile are reloaded within 1 second")
  @Order(2)
  public void zoneFileReloads()
      throws UnknownHostException, TextParseException, InterruptedException {
    List<String> ips = dnsTestClient.resolveArecords("worker.east.example.test");
    Assertions.assertThat(ips).hasSize(2);
    Assertions.assertThat(ips).contains(ip(EAST_WORKER_1), ip(EAST_WORKER_2));

    // remove an A Record
    corednsContainer.getZoneFile().removeRecord(EAST_WORKER_1);
    corednsContainer.getZoneFile().incrementSOASerial();
    corednsContainer.updateZoneFile();

    // check that the change is visible in 1 second
    Thread.sleep(Duration.ofSeconds(1).toMillis());
    ips = dnsTestClient.resolveArecords("worker.east.example.test");
    Assertions.assertThat(ips).hasSize(1);
    Assertions.assertThat(ips).contains(ip(EAST_WORKER_2));

    // add an A Record
    corednsContainer.getZoneFile().addRecord(EAST_WORKER_3);
    corednsContainer.getZoneFile().incrementSOASerial();
    corednsContainer.updateZoneFile();

    // check that the change is visible in 1 second
    Thread.sleep(Duration.ofSeconds(1).toMillis());
    ips = dnsTestClient.resolveArecords("worker.east.example.test");
    Assertions.assertThat(ips).hasSize(2);
    Assertions.assertThat(ips).contains(ip(EAST_WORKER_2), ip(EAST_WORKER_3));
  }

  @Test
  @DisplayName("Invalid domains don't resolve")
  @Order(1)
  public void testResolveARecordInvalidDomain() {
    var ips = dnsTestClient.resolveArecords("invalid-domain.test");
    assertEquals(0, ips.size());
  }

  private String ip(ARecord aRecord) {
    return aRecord.getAddress().getHostAddress();
  }
}
