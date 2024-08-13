package com.example.discovery;

import java.net.InetSocketAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DnsService {

    private final Resolver resolver;

    public DnsService(
        @Value("${dns.server.ip:#{null}}") String dnsServerIp,
        @Value("${dns.server.port:53}") int dnsServerPort) throws UnknownHostException {

        if (dnsServerIp == null) {
            // Fallback to the system's default nameserver
            dnsServerIp = getSystemDefaultNameserver();
        }

        try {
            resolver = new SimpleResolver(dnsServerIp);
            resolver.setPort(dnsServerPort);
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Failed to create resolver for DNS server: " + dnsServerIp);
        }
    }

    private String getSystemDefaultNameserver() throws UnknownHostException {
        // Fallback to the system's default nameserver
        List<InetSocketAddress> nameservers = ResolverConfig.getCurrentConfig().servers();
        if (nameservers.size() > 0) {
            return nameservers.get(0).getHostName();
        } else {
            throw new UnknownHostException("No system DNS nameserver found");
        }
    }

    public List<String> resolveARecord(String domain) {
        List<String> ipAddresses = new ArrayList<>();

        try {
            Lookup lookup = new Lookup(domain, Type.A);
            lookup.setResolver(resolver);
            Record[] records =lookup.run();
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
