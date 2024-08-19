package com.example;

import com.example.coredns.ZoneFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Master;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;

class ZoneFileTest {

  private static final String EXCPECTED_ZONE_FILE_STRING =
      """
        $TTL 3600
        example.test.   IN  SOA ns.example.test. admin.example.test. (
                1   ; Serial
                3600    ; Refresh
                1800    ; Retry
                604800  ; Expire
                86400   ; Minimum TTL
            )

        example.test.   IN  NS  ns.example.test.
        ns.example.test.  IN  A   192.168.1.1

        ; A records for east worker
        worker.east.example.test. IN  A   127.0.0.2
        worker.east.example.test. IN  A   127.0.0.1

        ; A records for west worker
        worker.west.example.test. IN  A   127.0.0.6
        worker.west.example.test. IN  A   127.0.0.7

        ; SRV records for api HTTP service running on worker.east and worker.west
        _api._tcp.worker.example.test. IN SRV 10 50 8080 worker.east.example.test.
        _api._tcp.worker.example.test. IN SRV 20 50 8080 worker.west.example.test.
        """;

  @Test
  @DisplayName("Zone File Generation")
  void testZoneFileGeneration() throws IOException {

    // generated file should match the excepted file
    ZoneFile zoneFile = generateZoneFile();
    List<Record> generatedRecords = parseZoneFile(zoneFile.generate());
    List<Record> expectedRecords = parseZoneFile(EXCPECTED_ZONE_FILE_STRING);

    Assertions.assertThat(generatedRecords).containsAll(expectedRecords);

    // Check that changes cause the match to fail
    zoneFile.incrementSOASerial();
    generatedRecords = parseZoneFile(zoneFile.generate());
    generatedRecords.removeAll(expectedRecords);

    Assertions.assertThat(generatedRecords).hasSize(1);
  }

  private List<Record> parseZoneFile(String zoneFileContent) throws IOException {
    InputStream inputStream =
        new ByteArrayInputStream(zoneFileContent.getBytes(StandardCharsets.UTF_8));
    Master master = new Master(inputStream);
    Record record;
    List<Record> records = new ArrayList<>();

    while ((record = master.nextRecord()) != null) {
      records.add(record);
    }

    return records;
  }

  private ZoneFile generateZoneFile() throws TextParseException, UnknownHostException {
    ZoneFile zoneFile = new ZoneFile("example.test", "192.168.1.1");

    // Adding A records
    zoneFile.addRecord(
        new ZoneFile.Builders.ARecord()
            .name("worker.east.example.test")
            .ip("127.0.0.1")
            .ttl(3600)
            .build());

    zoneFile.addRecord(
        new ZoneFile.Builders.ARecord()
            .name("worker.east.example.test")
            .ip("127.0.0.2")
            .ttl(3600)
            .build());

    zoneFile.addRecord(
        new ZoneFile.Builders.ARecord()
            .name("worker.west.example.test")
            .ip("127.0.0.6")
            .ttl(3600)
            .build());

    zoneFile.addRecord(
        new ZoneFile.Builders.ARecord()
            .name("worker.west.example.test")
            .ip("127.0.0.7")
            .ttl(3600)
            .build());

    // Adding SRV records
    zoneFile.addRecord(
        new SRVRecord(
            org.xbill.DNS.Name.fromString("_api._tcp.worker.example.test."),
            org.xbill.DNS.DClass.IN,
            3600,
            10,
            50,
            8080,
            org.xbill.DNS.Name.fromString("worker.east.example.test.")));

    zoneFile.addRecord(
        new SRVRecord(
            org.xbill.DNS.Name.fromString("_api._tcp.worker.example.test."),
            org.xbill.DNS.DClass.IN,
            3600,
            20,
            50,
            8080,
            org.xbill.DNS.Name.fromString("worker.west.example.test.")));

    return zoneFile;
  }
}
