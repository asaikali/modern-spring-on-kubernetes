package com.example.kubernetes;

import com.example.kubernetes.client.KubernetesClientProvider;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.apis.VersionApi;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.VersionInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/k")
public class ClientController {

  private final KubernetesClientProvider clientProvider;

  public ClientController(KubernetesClientProvider clientProvider) {
    this.clientProvider = clientProvider;
  }

  @GetMapping("/greetings")
  public Object getGreetings() throws IOException, ApiException {
    ApiClient client = this.clientProvider.getDefaultClient().get();
    CustomObjectsApi api = new CustomObjectsApi(client);

    // List all Greeting objects in the specified namespace
    Object result =
        api.listNamespacedCustomObject(
                "example.com", // group
                "v1", // version
                "greeter", // namespace
                "greetings" // plural of the CRD
                )
            .execute();

    return result;
  }

  @GetMapping("pods")
  public String getPods() throws IOException, ApiException {
    ApiClient client = this.clientProvider.getDefaultClient().get();

    CoreV1Api api = new CoreV1Api(client);
    V1PodList list = api.listNamespacedPod("kube-system").execute();

    StringBuilder pods = new StringBuilder();
    pods.append("pods in kube-system namespace on cluster at " + client.getBasePath() + "\n\n");
    list.getItems().forEach(pod -> pods.append(pod.getMetadata().getName()).append("\n"));
    return pods.toString();
  }

  @GetMapping("contexts")
  public Map<String, Map<String, String>> getClientsSummary() throws IOException {
    Map<String, Map<String, String>> summaries = new HashMap<>();
    List<String> contexts = clientProvider.getContexts();

    contexts.stream()
        .map(contextName -> Map.entry(contextName, clientProvider.getClientForContext(contextName)))
        .filter(entry -> entry.getValue().isPresent()) // Ensure the ApiClient is present
        .forEach(
            entry -> {
              String contextName = entry.getKey();
              ApiClient client = entry.getValue().get();
              VersionApi versionApi = new VersionApi(client);
              try {
                VersionInfo versionInfo = versionApi.getCode().execute();
                Map<String, String> summary = new HashMap<>();
                summary.put("apiServerUrl", client.getBasePath());
                summary.put("version", versionInfo.getGitVersion());
                summaries.put(contextName, summary);
              } catch (ApiException e) {
                e.printStackTrace();
              }
            });

    return summaries;
  }
}
