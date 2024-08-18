package com.example;

import java.net.InetAddress;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.SRVRecord;

public class DnsZoneFileGenerator {

  public static void main(String[] args) throws Exception {
    Name origin = Name.fromString("example.test.");
    long ttl = 3600;

    StringBuilder output = new StringBuilder();

    // Create SOA Record
    Name soaName = Name.fromString("ns.example.test.");
    Name adminName = Name.fromString("admin.example.test.");
    SOARecord soa =
        new SOARecord(origin, DClass.IN, ttl, soaName, adminName, 1, ttl, 1800, 604800, 86400);

    // Create NS Record
    NSRecord ns = new NSRecord(origin, DClass.IN, ttl, soaName);

    // Create A Records
    ARecord aRecordRoot = new ARecord(origin, DClass.IN, ttl, InetAddress.getByName("192.168.1.1"));
    ARecord aRecordNs =
        new ARecord(
            Name.fromString("ns.example.test."),
            DClass.IN,
            ttl,
            InetAddress.getByName("192.168.1.1"));

    // A records for worker.east
    ARecord[] eastWorkers =
        new ARecord[] {
          new ARecord(
              Name.fromString("worker.east.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.1")),
          new ARecord(
              Name.fromString("worker.east.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.2")),
          new ARecord(
              Name.fromString("worker.east.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.3")),
          new ARecord(
              Name.fromString("worker.east.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.4")),
          new ARecord(
              Name.fromString("worker.east.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.5")),
        };

    // A records for worker.west
    ARecord[] westWorkers =
        new ARecord[] {
          new ARecord(
              Name.fromString("worker.west.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.6")),
          new ARecord(
              Name.fromString("worker.west.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.7")),
          new ARecord(
              Name.fromString("worker.west.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.8")),
          new ARecord(
              Name.fromString("worker.west.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.9")),
          new ARecord(
              Name.fromString("worker.west.example.test."),
              DClass.IN,
              ttl,
              InetAddress.getByName("127.0.0.10")),
        };

    // SRV records for _api._tcp.worker
    SRVRecord[] srvRecords =
        new SRVRecord[] {
          new SRVRecord(
              Name.fromString("_api._tcp.worker.example.test."),
              DClass.IN,
              ttl,
              10,
              50,
              8080,
              Name.fromString("worker.east.example.test.")),
          new SRVRecord(
              Name.fromString("_api._tcp.worker.example.test."),
              DClass.IN,
              ttl,
              20,
              50,
              8080,
              Name.fromString("worker.west.example.test."))
        };

    // Build the output string
    output.append("$TTL 3600\n");
    output.append(soa.toString()).append("\n");
    output.append(ns.toString()).append("\n");
    output.append(aRecordRoot.toString()).append("\n");
    output.append(aRecordNs.toString()).append("\n");

    for (ARecord record : eastWorkers) {
      output.append(record.toString()).append("\n");
    }

    for (ARecord record : westWorkers) {
      output.append(record.toString()).append("\n");
    }

    for (SRVRecord record : srvRecords) {
      output.append(record.toString()).append("\n");
    }

    // Print the output
    System.out.println(output.toString());
  }
}
