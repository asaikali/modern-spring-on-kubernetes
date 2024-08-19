package com.example.coredns;

public class CoreFile {

  private final String domain;
  private final int port;
  private final String zoneFilePath;

  public CoreFile() {
    this("example.test", 53);
  }

  public CoreFile(String domain, int port) {
    this.domain = domain;
    this.port = port;
    this.zoneFilePath = "/db." + domain;
  }

  public int getPort() {
    return this.port;
  }

  public String getDomain() {
    return this.domain;
  }

  public String generate() {
    return """
        .:%s {
            log
            errors
            file %s %s {
              reload 1s
            }
        }
        """
        .formatted(this.port, this.zoneFilePath, this.domain);
  }

  public String getZoneFilePath() {
    return this.zoneFilePath;
  }
}
