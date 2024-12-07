package com.example;

import com.example.kubernetes.client.KubernetesClientProvider;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.ApiextensionsV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinition;
import io.kubernetes.client.util.Yaml;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class KubernetesCustomResourceApplicationTests {

  @Container
  static K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.31.0-k3s1"));

  @Autowired KubernetesClientProvider kubernetesClientProvider;

  @Test
  @Disabled
  void testCrdCreation() throws IOException {
    String kubeConfigYaml = k3s.getKubeConfigYaml();
    ApiClient client = kubernetesClientProvider.getClientFromKubeConfig(kubeConfigYaml).get();
    ApiextensionsV1Api apiInstance = new ApiextensionsV1Api(client);

    // create the crd
    List<Object> crds = Yaml.loadAll(new File("k8s/crd.yaml"));
    for (Object crdObject : crds) {
      if (crdObject instanceof V1CustomResourceDefinition crd) {
        apiInstance.createCustomResourceDefinition(crd);
        System.out.println("Created new CRD: " + crd.getMetadata().getName());
      }
    }

    // create the greeting objects
    CustomObjectsApi customObjectsApi = new CustomObjectsApi(client);
    List<Object> greetings = Yaml.loadAll(new File("k8s/greetings.yaml"));
    for (Object greetigObject : greetings) {
      customObjectsApi.createNamespacedCustomObject(
          "example.com", "v1", "greeter", "greetings", greetigObject);
    }
  }
}
