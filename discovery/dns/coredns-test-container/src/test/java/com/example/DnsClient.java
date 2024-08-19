package com.example;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsClient {

  private final Resolver resolver;

  public DnsClient(String dnsServerIp, int dnsServerPort) throws UnknownHostException {
    try {
      resolver = new SimpleResolver(dnsServerIp);
      resolver.setPort(dnsServerPort);
      resolver.setTCP(true);
    } catch (UnknownHostException e) {
      throw new UnknownHostException("Failed to create resolver for DNS server: " + dnsServerIp);
    }
  }

  public List<String> resolveArecords(String domain) {
    List<String> ipAddresses = new ArrayList<>();

    try {
      Lookup lookup = new Lookup(domain, Type.A);
      lookup.setResolver(resolver);

      // Disable DNSJava caching by setting a cache with TTL of 0
      Cache noCache = new Cache();
      noCache.setMaxCache(0); // Disable positive cache
      noCache.setMaxNCache(0); // Disable negative cache
      lookup.setCache(noCache);

      Record[] records = lookup.run();
      if (records != null) {
        for (Record record : records) {
          ARecord aRecord = (ARecord) record;
          ipAddresses.add(aRecord.getAddress().getHostAddress());
        }
      }
    } catch (TextParseException e) {
      throw new RuntimeException("Failed to parse domain name: " + domain);
    }

    return ipAddresses;
  }
}
