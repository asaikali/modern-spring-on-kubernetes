package dns;

import java.net.InetAddress;
import org.xbill.DNS.*;

import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.Record;

public class Zone {
    private final Name origin;
    private final long ttl;
    private final List<Record> records = new ArrayList<>();

    public Zone(String origin) throws TextParseException {
        this(origin, 1);
    }

    public Zone(String origin, long ttl) throws TextParseException {
        this.origin = Name.fromString(origin);
        this.ttl = ttl;
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public List<Record> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("$TTL ").append(ttl).append("\n");
        for (Record record : records) {
            output.append(record.toString()).append("\n");
        }
        return output.toString();
    }

    public static void main(String[] args) throws Exception {
        String origin = "example.test.";

        Zone zone = new Zone(origin);

        // Using Builder to create an SOARecord
        SOARecord soa = new Builders.SOARecord()
            .name(origin)
            .primaryNS("ns.example.test.")
            .admin("admin.example.test.")
            .serial(1)
            .refresh(3600)
            .retry(1800)
            .expire(604800)
            .minimum(86400)
            .build();

        zone.addRecord(soa);

        // Using Builder to create an ARecord for root and ns
        ARecord aRecordRoot = new Builders.ARecord()
            .name(origin)
            .ip("192.168.1.1")
            .build();

        ARecord aRecordNs = new Builders.ARecord()
            .name("ns.example.test.")
            .ip("192.168.1.1")
            .build();

        zone.addRecord(aRecordRoot);
        zone.addRecord(aRecordNs);

        // A records for worker.east
        ARecord[] eastWorkers = new ARecord[]{
            new Builders.ARecord().name("worker.east.example.test.").ip("127.0.0.1").build(),
            new Builders.ARecord().name("worker.east.example.test.").ip("127.0.0.2").build(),
            new Builders.ARecord().name("worker.east.example.test.").ip("127.0.0.3").build(),
            new Builders.ARecord().name("worker.east.example.test.").ip("127.0.0.4").build(),
            new Builders.ARecord().name("worker.east.example.test.").ip("127.0.0.5").build(),
        };
        for (ARecord record : eastWorkers) {
            zone.addRecord(record);
        }

        // A records for worker.west
        ARecord[] westWorkers = new ARecord[]{
            new Builders.ARecord().name("worker.west.example.test.").ip("127.0.0.6").build(),
            new Builders.ARecord().name("worker.west.example.test.").ip("127.0.0.7").build(),
            new Builders.ARecord().name("worker.west.example.test.").ip("127.0.0.8").build(),
            new Builders.ARecord().name("worker.west.example.test.").ip("127.0.0.9").build(),
            new Builders.ARecord().name("worker.west.example.test.").ip("127.0.0.10").build(),
        };
        for (ARecord record : westWorkers) {
            zone.addRecord(record);
        }

        // Output the entire zone file as a string
        System.out.println(zone);
    }

    // Inner class for Builders
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
                this.name = Name.fromString(name);
                return this;
            }

            public SOARecord ttl(long ttl) {
                this.ttl = ttl;
                return this;
            }

            public SOARecord primaryNS(String primaryNS) throws TextParseException {
                this.primaryNS = Name.fromString(primaryNS);
                return this;
            }

            public SOARecord admin(String admin) throws TextParseException {
                this.admin = Name.fromString(admin);
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
                return new org.xbill.DNS.SOARecord(name, DClass.IN, ttl, primaryNS, admin, serial, refresh, retry, expire, minimum);
            }
        }

        public static class ARecord {
            private static final long DEFAULT_TTL = 1;
            private Name name;
            private long ttl = DEFAULT_TTL;
            private InetAddress ip;

            public ARecord name(String name) throws TextParseException {
                this.name = Name.fromString(name);
                return this;
            }

            public ARecord ttl(long ttl) {
                this.ttl = ttl;
                return this;
            }

            public ARecord ip(String ip) throws Exception {
                this.ip = InetAddress.getByName(ip);
                return this;
            }

            public org.xbill.DNS.ARecord build() {
                return new org.xbill.DNS.ARecord(name, DClass.IN, ttl, ip);
            }
        }
    }
}
