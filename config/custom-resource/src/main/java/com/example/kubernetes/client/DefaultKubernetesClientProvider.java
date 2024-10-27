package com.example.kubernetes.client;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class DefaultKubernetesClientProvider implements KubernetesClientProvider {

  private final String kubeConfigPath;

  // Constructor injection for kubeConfigPath
  public DefaultKubernetesClientProvider(
      @Value("${kubernetes.config.path:${user.home}/.kube/config}") String kubeConfigPath) {
    this.kubeConfigPath = kubeConfigPath;
  }

  private KubeConfig loadKubeConfig() throws IOException {
    FileReader configReader = new FileReader(kubeConfigPath);
    return KubeConfig.loadKubeConfig(configReader);
  }

  private ApiClient createApiClientFromContext(String contextName) throws IOException {
    KubeConfig config = loadKubeConfig();
    config.setContext(contextName);
    return ClientBuilder.kubeconfig(config).build();
  }

  @Override
  public Optional<ApiClient> getClientFromKubeConfig(String kubeConfigYaml) {
    try {
      KubeConfig config = KubeConfig.loadKubeConfig(new StringReader(kubeConfigYaml));
      ApiClient client = ClientBuilder.kubeconfig(config).build();
      return Optional.of(client);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<ApiClient> getDefaultClient() {
    try {
      ApiClient client = ClientBuilder.defaultClient();
      return Optional.of(client);
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<ApiClient> getClientForContext(String contextName) {
    try {
      ApiClient client = createApiClientFromContext(contextName);
      return Optional.of(client);
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<String> getContexts() {

    try {
      KubeConfig config = loadKubeConfig();
      List<Object> contexts = config.getContexts();
      List<String> result = new ArrayList<>();
      for (Object context : contexts) {
        String name = ((Map<String, String>) context).get("name");
        result.add(name);
      }
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
