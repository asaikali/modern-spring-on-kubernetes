package com.example.greeter;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

// GreetingResource Custom Resource
class GreetingResource implements KubernetesObject {
  @SerializedName("apiVersion")
  private String apiVersion = "example.com/v1";

  @SerializedName("kind")
  private String kind = "GreetingResource";

  @SerializedName("metadata")
  private V1ObjectMeta metadata;

  @SerializedName("spec")
  private GreetingSpec spec;

  public String getApiVersion() {
    return apiVersion;
  }

  public String getKind() {
    return kind;
  }

  public V1ObjectMeta getMetadata() {
    return metadata;
  }

  public GreetingSpec getSpec() {
    return spec;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setMetadata(V1ObjectMeta metadata) {
    this.metadata = metadata;
  }

  public void setSpec(GreetingSpec spec) {
    this.spec = spec;
  }

  public static class GreetingSpec {
    @SerializedName("message")
    private String message;

    @SerializedName("language")
    private String language;

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }
  }
}
