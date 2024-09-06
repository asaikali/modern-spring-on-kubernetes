package com.example.coredns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.xbill.DNS.*;

public class ZoneFile {
  private final Name origin;
  private final long ttl;
  private SOARecord soa;
  private final NSRecord nsRecord;
  private final ARecord nsARecord;
  private final List<ARecord> aRecords = new ArrayList<>();
  private final List<SRVRecord> srvRecords = new ArrayList<>();

  public ZoneFile(String domain, List<ARecord> aRecords)
      throws UnknownHostException, TextParseException {
    this(domain, "192.168.1.1");
    this.addRecord(aRecords.toArray(new ARecord[aRecords.size()]));
  }

  public ZoneFile(String domain, String nsIpAddress)
      throws TextParseException, UnknownHostException {
    this(domain, nsIpAddress, 1);
  }

  public ZoneFile(String domain, String nsIpAddress, long ttl)
      throws TextParseException, UnknownHostException {
    // Automatically add the trailing period if not provided
    this.origin = Name.fromString(ensureTrailingDot(domain));
    this.ttl = ttl;

    // Create the NSRecord with ns.origin
    String nsName = "ns." + origin.toString();
    this.nsRecord = new Builders.NSRecord().name(origin.toString()).ttl(ttl).target(nsName).build();

    // Create the corresponding ARecord for the NSRecord
    this.nsARecord = new Builders.ARecord().name(nsName).ip(nsIpAddress).ttl(ttl).build();

    // Create the SOA record
    this.soa =
        new Builders.SOARecord()
            .name(origin.toString())
            .primaryNS(nsRecord.getTarget().toString())
            .admin("admin." + origin.toString())
            .serial(1)
            .refresh(3600)
            .retry(1800)
            .expire(604800)
            .minimum(86400)
            .build();
  }

  public void addRecord(ARecord... records) {
    aRecords.addAll(Arrays.asList(records));
  }

  public boolean removeRecord(ARecord record) {
    return aRecords.remove(record);
  }

  public void addRecord(SRVRecord... records) {
    srvRecords.addAll(Arrays.asList(records));
  }

  public boolean removeRecord(SRVRecord record) {
    return srvRecords.remove(record);
  }

  public void incrementSOASerial() throws TextParseException {
    long currentSerial = soa.getSerial();
    soa =
        new Builders.SOARecord()
            .name(soa.getName().toString())
            .primaryNS(soa.getHost().toString())
            .admin(soa.getAdmin().toString())
            .serial(currentSerial + 1)
            .refresh(soa.getRefresh())
            .retry(soa.getRetry())
            .expire(soa.getExpire())
            .minimum(soa.getMinimum())
            .ttl(ttl)
            .build();
  }

  private static String ensureTrailingDot(String domain) {
    return domain.endsWith(".") ? domain : domain + ".";
  }

  public String generate() {
    StringBuilder output = new StringBuilder();

    // Comment explaining the TTL setting
    output.append(
        """
        ; =========================
        ; Time To Live (TTL) Setting
        ; =========================
        ; TTL: Specifies the default time (in seconds) that DNS records are cached by resolvers.
        ; Explanation:
        ; - This value determines how long DNS resolvers should cache the records from this zone file.
        ; - A lower TTL means changes to the DNS records will propagate more quickly, but it may increase the load on the DNS server.
        ; - A higher TTL reduces the load on the DNS server but means changes take longer to propagate.
        ;
        """);
    output.append("$TTL ").append(ttl).append("\n\n");

    // Comment explaining the SOA record section
    output.append(
        """
        ; =========================
        ; Start of Authority (SOA) Record
        ; =========================
        ; SOA Record: Contains crucial administrative information about the domain.
        ; Structure of the SOA Record:
        ; <Domain>        <TTL> <Class> <Type> <Primary NS> <Admin> <Serial> <Refresh> <Retry> <Expire> <Minimum TTL>
        ;
        ; Explanation:
        ; - <Domain>: The domain name for which this SOA record applies.
        ; - <TTL>: The Time To Live for this record. If not specified, the global TTL is used.
        ; - <Class>: The DNS class (typically "IN" for Internet).
        ; - <Type>: The type of record, which is "SOA" here.
        ; - <Primary NS>: The primary name server for the domain.
        ; - <Admin>: The administrator's email (formatted with a '.' instead of '@').
        ; - <Serial>: A version number for the zone file, which should be incremented with each change.
        ; - <Refresh>: Time before secondary name servers should check for updates from the primary.
        ; - <Retry>: Time to wait before retrying a failed update.
        ; - <Expire>: Time after which the zone is no longer authoritative if no updates are received.
        ; - <Minimum TTL>: The minimum time that records from this zone should be cached by DNS resolvers.
        ;
        """);
    output.append(soa.toString()).append("\n\n"); // Blank line after SOA record

    // Comment explaining the NS record
    output.append(
        """
        ; =========================
        ; Name Server (NS) Record
        ; =========================
        ; NS Record: Specifies the authoritative name server for the domain.
        ; Structure of the NS Record:
        ; <Domain>        <TTL> <Class> <Type> <Name Server>
        ;
        ; Explanation:
        ; - <Domain>: The domain for which this NS record applies.
        ; - <TTL>: The Time To Live for this record. If not specified, the global TTL is used.
        ; - <Class>: The DNS class (typically "IN" for Internet).
        ; - <Type>: The type of record, which is "NS" here.
        ; - <Name Server>: The domain name of the authoritative name server for the domain.
        ;
        """);
    output.append(nsRecord.toString()).append("\n\n");

    // Comment explaining A records
    output.append(
        """
        ; =========================
        ; Address (A) Records
        ; =========================
        ; A Record: Maps a domain name (or subdomain) to an IP address.
        ; Structure of the A Record:
        ; <Domain>        <TTL> <Class> <Type> <IP Address>
        ;
        ; Explanation:
        ; - <Domain>: The domain or subdomain this A record applies to.
        ; - <TTL>: The Time To Live for this record. If not specified, the global TTL is used.
        ; - <Class>: The DNS class (typically "IN" for Internet).
        ; - <Type>: The type of record, which is "A" here.
        ; - <IP Address>: The IPv4 address that the domain or subdomain should resolve to.
        ;
        """);
    output.append(nsARecord.toString()).append("\n");
    for (ARecord aRecord : aRecords) {
      output.append(aRecord.toString()).append("\n");
    }
    output.append("\n"); // Blank line after A records

    if (!srvRecords.isEmpty()) {
      // Comment explaining the SRV records section
      output.append(
          """
            ; =========================
            ; Service (SRV) Records
            ; =========================
            ; SRV Record: Specifies the location of servers for specific services (like SIP, LDAP, etc.).
            ; Structure of the SRV Record:
            ; <Domain>        <TTL> <Class> <Type> <Priority> <Weight> <Port> <Target>
            ;
            ; Explanation:
            ; - <Domain>: The domain or subdomain this SRV record applies to.
            ; - <TTL>: The Time To Live for this record. If not specified, the global TTL is used.
            ; - <Class>: The DNS class (typically "IN" for Internet).
            ; - <Type>: The type of record, which is "SRV" here.
            ; - <Priority>: Lower values indicate higher priority.
            ; - <Weight>: Used for load balancing among servers with the same priority; higher values are preferred.
            ; - <Port>: The port on which the service is running.
            ; - <Target>: The domain name of the server providing the service.
            ;
            """);
      for (SRVRecord srvRecord : srvRecords) {
        output.append(srvRecord.toString()).append("\n");
      }
    }

    return output.toString();
  }

  public static class Builders {

    public static class SOARecord {
      private static final long DEFAULT_TTL = 1;
      private Name name;
      private long ttl = DEFAULT_TTL;
      private Name primaryNS;
      private Name admin;
      private long serial;
      private long refresh;
      private long retry;
      private long expire;
      private long minimum;

      public SOARecord name(String name) throws TextParseException {
        this.name = Name.fromString(ensureTrailingDot(name));
        return this;
      }

      public SOARecord ttl(long ttl) {
        this.ttl = ttl;
        return this;
      }

      public SOARecord primaryNS(String primaryNS) throws TextParseException {
        this.primaryNS = Name.fromString(ensureTrailingDot(primaryNS));
        return this;
      }

      public SOARecord admin(String admin) throws TextParseException {
        this.admin = Name.fromString(ensureTrailingDot(admin));
        return this;
      }

      public SOARecord serial(long serial) {
        this.serial = serial;
        return this;
      }

      public SOARecord refresh(long refresh) {
        this.refresh = refresh;
        return this;
      }

      public SOARecord retry(long retry) {
        this.retry = retry;
        return this;
      }

      public SOARecord expire(long expire) {
        this.expire = expire;
        return this;
      }

      public SOARecord minimum(long minimum) {
        this.minimum = minimum;
        return this;
      }

      public org.xbill.DNS.SOARecord build() {
        return new org.xbill.DNS.SOARecord(
            name, DClass.IN, ttl, primaryNS, admin, serial, refresh, retry, expire, minimum);
      }
    }

    public static class ARecord {
      private static final long DEFAULT_TTL = 1;
      private Name name;
      private long ttl = DEFAULT_TTL;
      private InetAddress ip;

      public ARecord name(String name) throws TextParseException {
        this.name = Name.fromString(ensureTrailingDot(name));
        return this;
      }

      public ARecord ttl(long ttl) {
        this.ttl = ttl;
        return this;
      }

      public ARecord ip(String ip) {
        try {
          this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
          throw new RuntimeException(e);
        }
        return this;
      }

      public org.xbill.DNS.ARecord build() {
        return new org.xbill.DNS.ARecord(name, DClass.IN, ttl, ip);
      }
    }

    public static class NSRecord {
      private static final long DEFAULT_TTL = 1;
      private Name name;
      private long ttl = DEFAULT_TTL;
      private Name target;

      public NSRecord name(String name) throws TextParseException {
        this.name = Name.fromString(ensureTrailingDot(name));
        return this;
      }

      public NSRecord ttl(long ttl) {
        this.ttl = ttl;
        return this;
      }

      public NSRecord target(String target) throws TextParseException {
        this.target = Name.fromString(ensureTrailingDot(target));
        return this;
      }

      public org.xbill.DNS.NSRecord build() {
        return new org.xbill.DNS.NSRecord(name, DClass.IN, ttl, target);
      }
    }
  }
}
