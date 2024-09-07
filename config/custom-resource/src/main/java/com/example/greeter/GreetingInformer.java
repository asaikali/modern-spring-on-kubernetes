package com.example.greeter;

import com.example.kubernetes.client.KubernetesClientProvider;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * The GreetingInformer class demonstrates how to use Kubernetes Informers in Java to efficiently
 * monitor and cache the state of Kubernetes resources, specifically a custom resource called
 * Greeting.
 *
 * <p>Informers are a high-level construct in Kubernetes client libraries that watch for changes in
 * resource state (e.g., add, update, delete) and maintain a local cache of resources for efficient
 * lookup and event-driven programming.
 *
 * <p>This class is part of a service designed to watch custom Greeting resources, automatically
 * updating the local cache when resources are added, modified, or deleted.
 */
@Service
class GreetingInformer {

  private final ApiClient client;
  private final SharedIndexInformer<GreetingResource> greetingInformer;


  /**
   * Retrieves all GreetingResource objects from the local cache. This method uses the Lister to
   * access the cache maintained by the Informer, ensuring fast and efficient access to the current
   * state of resources.
   *
   * <p>A **Lister** is a utility class that simplifies access to the local cache of resources
   * maintained by an Informer. Instead of making API calls to the Kubernetes server, the Lister
   * interacts directly with the cached data, allowing for fast and efficient lookups. The Lister
   * ensures that the resources in the cache are consistent and up-to-date as the Informer keeps the
   * cache synchronized with changes in the cluster (e.g., additions, updates, deletions).
   *
   * <p>By using the Lister, you can query Kubernetes resources by name or filter them using
   * specific criteria (in this case, the language of the Greeting resource) without needing to
   * interact with the API server. This improves the performance of your application by avoiding
   * unnecessary API requests and providing real-time access to resources.
   *
   * @return a list of all cached GreetingResource objects
   */
  public List<GreetingResource> getGreetings() {
    // Use the Lister to read objects from the cache maintained by the informer
    Lister<GreetingResource> greetingLister = new Lister<>(greetingInformer.getIndexer());
    return greetingLister.list();
  }

  /**
   * Retrieves a GreetingResource from the local cache, filtering by the specified language.
   *
   * @param language the language to filter the Greeting resources by
   * @return an Optional containing the matching GreetingResource, if found
   */
  public Optional<GreetingResource> getGreeting(String language) {
    // Filter the cached resources to find the first one that matches the specified language
    Optional<GreetingResource> greetingResource = this.getGreetings().stream()
        .filter(object -> language.equals(object.getSpec().getLanguage()))
        .findFirst();

    return greetingResource;
  }

  /**
   * Initializes the GreetingInformer, setting up an Informer to watch for changes to Greeting
   * resources in the Kubernetes cluster. The Informer is responsible for maintaining an up-to-date
   * cache of these resources, while event handlers allow responding to changes such as resource
   * additions, updates, and deletions.
   *
   * @param clientProvider the provider for the Kubernetes API client
   */
  GreetingInformer(KubernetesClientProvider clientProvider) {
    this.client = clientProvider.getDefaultClient().orElseThrow();

    // Set up a generic Kubernetes API for the Greeting custom resource
    GenericKubernetesApi<GreetingResource, GreetingResourceList> greetingApi =
        new GenericKubernetesApi<>(
            GreetingResource.class,
            GreetingResourceList.class,
            "example.com", // group
            "v1", // version
            "greetings", // plural (the resource name in plural form)
            client);

    // Create a factory for managing shared informers
    SharedInformerFactory informerFactory = new SharedInformerFactory();

    // Create an informer for GreetingResource with a resync period of 10 seconds (10_000 ms)
    greetingInformer =
        informerFactory.sharedIndexInformerFor(
            greetingApi, GreetingResource.class, 10_000); // Resync period in milliseconds

    // Add event handlers to the informer to handle resource events like add, update, and delete
    greetingInformer.addEventHandler(new EventHandler());

    // Start the informer factory to begin watching and caching resources
    informerFactory.startAllRegisteredInformers();
  }

  /**
   * The EventHandler class defines how to handle events related to the Greeting resources. This is
   * where you define the logic for what to do when a resource is added, updated, or deleted.
   */
  private static class EventHandler implements ResourceEventHandler<GreetingResource> {

    /**
     * Called when a Greeting resource is added. This method will print the message of the added
     * resource.
     *
     * @param greeting the added GreetingResource object
     */
    @Override
    public void onAdd(GreetingResource greeting) {
      System.out.println("Greeting added: " + greeting.getSpec().getMessage());
    }

    /**
     * Called when a Greeting resource is updated. This method will print the message of the updated
     * resource.
     *
     * @param oldGreeting the original GreetingResource before the update
     * @param newGreeting the updated GreetingResource after the update
     */
    @Override
    public void onUpdate(GreetingResource oldGreeting, GreetingResource newGreeting) {
      System.out.println("Greeting updated: " + newGreeting.getSpec().getMessage());
    }

    /**
     * Called when a Greeting resource is deleted. This method will print the name of the deleted
     * resource.
     *
     * @param greeting the deleted GreetingResource object
     * @param deletedFinalStateUnknown true if the final state of the deleted resource is unknown
     */
    @Override
    public void onDelete(GreetingResource greeting, boolean deletedFinalStateUnknown) {
      System.out.println("Greeting deleted: " + greeting.getMetadata().getName());
    }
  }
}
