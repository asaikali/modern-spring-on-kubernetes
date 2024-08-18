package com.example;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import java.util.Map;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.com.google.common.base.Preconditions;
import org.testcontainers.utility.DockerImageName;

class CoreDnsContainer extends GenericContainer<CoreDnsContainer> {

  public CoreDnsContainer(String dockerImageName) {
    super(DockerImageName.parse(dockerImageName));
    this.withClasspathResourceMapping("Corefile", "/Corefile", BindMode.READ_ONLY)
        .withClasspathResourceMapping("db.example.test", "/db.example.test", BindMode.READ_ONLY)
        .withExposedPorts(53)
        .withCommand("-conf", "/Corefile");
  }

  public Integer getMappedPort(ExposedPort exposedPort) {
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
