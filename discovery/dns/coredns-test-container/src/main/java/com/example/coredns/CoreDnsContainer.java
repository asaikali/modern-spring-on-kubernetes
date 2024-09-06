package com.example.coredns;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.com.google.common.base.Preconditions;
import org.testcontainers.utility.DockerImageName;

public class CoreDnsContainer extends GenericContainer<CoreDnsContainer> {

  private final CoreFile coreFile;
  private final ZoneFile zoneFile;
  private final Path coreFilePath;
  private final Path zoneFilePath;

  public CoreDnsContainer(CoreFile coreFile, ZoneFile zoneFile) {
    this("coredns/coredns:1.11.1", coreFile, zoneFile);
  }

  public CoreDnsContainer(String dockerImageName, CoreFile coreFile, ZoneFile zoneFile) {
    super(DockerImageName.parse(dockerImageName));
    try {
      // write the corefile
      this.coreFile = coreFile;
      this.coreFilePath = Files.createTempFile("coredns-corefile", ".txt");
      Files.writeString(this.coreFilePath, coreFile.generate());

      // write the zonefile
      this.zoneFile = zoneFile;
      this.zoneFilePath = Files.createTempFile("coredns-zonefile", ".txt");
      this.updateZoneFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.withFileSystemBind(coreFilePath.toString(), "/Corefile", BindMode.READ_ONLY)
        .withFileSystemBind(zoneFilePath.toString(), coreFile.getZoneFilePath(), BindMode.READ_ONLY)
        .withExposedPorts(coreFile.getPort())
        .withCommand("-conf", "/Corefile");
  }

  public ZoneFile getZoneFile() {
    return zoneFile;
  }

  public void updateZoneFile() {
    try {
      Files.writeString(this.zoneFilePath, zoneFile.generate());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Integer getDnsTcpPort() {
    return getMappedPort(ExposedPort.tcp(this.coreFile.getPort()));
  }

  private Integer getMappedPort(ExposedPort exposedPort) {
    Preconditions.checkState(
        this.getContainerId() != null,
        "Mapped port can only be obtained after the container is started");
    InspectContainerResponse containerInfo = this.getContainerInfo();
    if (containerInfo == null) {
      throw new RuntimeException(
          String.format("Container with id '%s' not found ", this.getContainerId()));
    }

    NetworkSettings networkSettings = containerInfo.getNetworkSettings();
    Ports ports = networkSettings.getPorts();
    Map<ExposedPort, Binding[]> bindings = ports.getBindings();
    Binding[] binding = bindings.get(exposedPort);

    if (binding != null && binding.length > 0 && binding[0] != null) {
      return Integer.valueOf(binding[0].getHostPortSpec());
    } else {
      throw new IllegalArgumentException(
          "Requested port (" + exposedPort.getPort() + ") is not mapped");
    }
  }
}
