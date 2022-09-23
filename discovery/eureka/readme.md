# Service Discovery Eureka Basics

### Overview

This sample shows off how to use Spring Cloud Eureka for service discovery between 
microservices. In this sample it will be shown how `message-service` registers itself inside 
eureka, while `billboard-client` is able to resolve the endpoint for `message-service` from the 
eureka registry by looking it up by its service name.

### Run the Demo

* Import the root of the repo into your favorite Java IDE
* Run eureka-server application
* Run `message-service` application
* Browse to http://localhost:8761 and confirm that `message-service` is showing up in the eureka registry
* Run `billboard-client` application and browse it to it in your browser


### Things to try out
* Examine `BillboardController` in `billboard-client` to view how the invocation URL is based on application name instead of actual endpoint. The hostname will be automatically substituted by Eureka
* Browse to eureka dashboard at http://localhost:8761. Observe that only the `message-service` is showing up, as `billboard-client` only fetches registry, but doesn't advertise itself
* Run multiple instances of `message-service`. Observe how a single service have multiple instance URLs in eureka dashboard. The calling application will do client-side load balancing by selecting a random instance when calling it.

### Deploy to K8s

* build the container images locally using spring boot plugin `./mvnw clean spring-boot:build-image`.
* deploy the app `kustomize build . | kubectl apply -f -`
* check contents of the eureka namespace `k get all -n eureka` you will see the node ports of all the
  services for the greeter, billboard, and eureka. Output should be similar to the one below.

### Resources to Learn More:
* https://cloud.spring.io/spring-cloud-netflix/