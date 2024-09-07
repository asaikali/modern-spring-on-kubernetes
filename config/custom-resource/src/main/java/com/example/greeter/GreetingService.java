package com.example.greeter;

import com.example.json.JsonService;
import com.example.kubernetes.client.KubernetesClientProvider;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.apis.CustomObjectsApi.APIlistNamespacedCustomObjectRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

  private final JsonService jsonService;
  private final KubernetesClientProvider clientProvider;
  private final String namespace;
  private final GreetingInformer greetingInformer;

  public GreetingService(
      GreetingInformer greetingInformer,
      JsonService jsonService,
      KubernetesClientProvider clientProvider,
      @Value("${namespace:greeter}") String namespace) {
    this.jsonService = jsonService;
    this.clientProvider = clientProvider;
    this.namespace = namespace;
    this.greetingInformer = greetingInformer;
  }

  public List<Greeting> getAllGreetings() {

    // List all GreetingResource objects in the specified namespace
    try {
      ApiClient client = this.clientProvider.getDefaultClient().get();
      CustomObjectsApi api = new CustomObjectsApi(client);
      APIlistNamespacedCustomObjectRequest request =
          api.listNamespacedCustomObject(
              "example.com", // group
              "v1", // version
              namespace, // namespace
              "greetings" // plural of the CRD
              );

      Object greetingsResponse = request.execute();
      String greetingsJson = this.jsonService.toJson(greetingsResponse);
      GreetingResourceList greetingResourceList =
          this.jsonService.fromJson(greetingsJson, GreetingResourceList.class);
      List<GreetingResource> greetingResources = greetingResourceList.getItems();
      List<Greeting> result =
          greetingResources.stream()
              .map(
                  object ->
                      new Greeting(object.getSpec().getMessage(), object.getSpec().getLanguage()))
              .toList();
      return result;
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  public String getGreetingMessage(String language) {
    Optional<GreetingResource> greetingResource = this.greetingInformer.getGreeting(language);
    Optional<String> greeting = greetingResource.map(object -> object.getSpec().getMessage());
    return greeting.orElse("Backup greeting in English");
  }
}
