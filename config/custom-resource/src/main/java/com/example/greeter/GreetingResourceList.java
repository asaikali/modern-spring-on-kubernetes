package com.example.greeter;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.openapi.models.V1ListMeta;
import java.util.List;

// GreetingResourceList Custom Resource List
class GreetingResourceList implements KubernetesListObject {
  @SerializedName("apiVersion")
  private String apiVersion = "example.com/v1";

  @SerializedName("kind")
  private String kind = "GreetingResourceList";

  @SerializedName("metadata")
  private V1ListMeta metadata;

  @SerializedName("items")
  private List<GreetingResource> items;

  public String getApiVersion() {
    return apiVersion;
  }

  public String getKind() {
    return kind;
  }

  public V1ListMeta getMetadata() {
    return metadata;
  }

  public List<GreetingResource> getItems() {
    return items;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setMetadata(V1ListMeta metadata) {
    this.metadata = metadata;
  }

  public void setItems(List<GreetingResource> items) {
    this.items = items;
  }
}
