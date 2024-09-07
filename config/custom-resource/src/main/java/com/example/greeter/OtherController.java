package com.example.greeter;

import com.example.json.JsonService;
import com.example.kubernetes.client.KubernetesClientProvider;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.apis.CustomObjectsApi.APIlistNamespacedCustomObjectRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/other")
public class OtherController {
  private final KubernetesClientProvider clientProvider;
  private final String namespace;
  private final JsonService jsonService;

  public OtherController(
      JsonService jsonService,
      KubernetesClientProvider clientProvider,
      @Value("${namespace:greeter}") String namespace) {
    this.clientProvider = clientProvider;
    this.namespace = namespace;
    this.jsonService = jsonService;
  }

  /**
   * This code demonstrates a low level of reading the state of greetings objects directly form the
   * api without going through an informer.
   *
   * @return
   */
  @GetMapping("greeting")
  public List<Greeting> getAllGreetingsNoCache() {

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
}
