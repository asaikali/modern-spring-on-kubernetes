package com.example.kubernetes.client;

import io.kubernetes.client.openapi.ApiClient;
import java.util.List;
import java.util.Optional;

/**
 * Interface that provides methods for managing and retrieving {@link ApiClient} instances. {@link
 * ApiClient} is used to interact with Kubernetes clusters.
 *
 * <p>This interface defines methods to:
 *
 * <ul>
 *   <li>Retrieve the default Kubernetes {@link ApiClient}.
 *   <li>Retrieve a specific {@link ApiClient} for a given Kubernetes context name.
 *   <li>Retrieve a list of all available Kubernetes contexts from a kubeconfig file.
 * </ul>
 */
public interface KubernetesClientProvider {

  Optional<ApiClient> getClientFromKubeConfig(String kubeConfigYaml);

  /**
   * Returns the default {@link ApiClient} to interact with the Kubernetes API.
   *
   * @return an {@link Optional} containing the default {@link ApiClient}, or an empty {@link
   *     Optional} if no default client is available.
   */
  Optional<ApiClient> getDefaultClient();

  /**
   * Returns the {@link ApiClient} associated with a specific Kubernetes context, identified by the
   * context name in the kubeconfig file.
   *
   * @param contextName the name of the Kubernetes context to retrieve the client for.
   * @return an {@link Optional} containing the {@link ApiClient} for the specified context, or an
   *     empty {@link Optional} if the client for that context is not available.
   */
  Optional<ApiClient> getClientForContext(String contextName);

  /**
   * Returns a list of all available Kubernetes contexts from the current kubeconfig file.
   *
   * @return a {@link List} of context names available in the kubeconfig file.
   */
  List<String> getContexts();
}
