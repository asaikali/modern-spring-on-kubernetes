# modern-spring-on-kubernetes

**Modern Spring Based Microservices on Kubernetes**

Example applications show to use Spring Boot on Kubernetes. There are multiple types
of sample applications in this repo. Samples showing various approaches for
containerizing Spring applications in the `containerize` folder.
Samples showing features for running Spring applications on Kubernetes in `run`
folder. Samples showing features for AOT in the `aot` folder. Each sample application has
a `README.md` that explains how to run the
sample and points out interesting things to look at.

## Software Prerequisites

### Java development tooling

* [Java 21 JDK](https://sdkman.io/)
* [Java 21 GraalVM](https://sdkman.io/)
* [Maven](https://maven.apache.org/index.html)
* [Gradle](https://gradle.org/)
* Favourite Java IDE one of
    * [Eclipse Spring Tool Suite](https://spring.io/tools)
    * [IntelliJ](https://www.jetbrains.com/idea/download)
    * [VSCode](https://code.visualstudio.com/)

### Containerization tools

* [Docker](https://www.docker.com/products/docker-desktop)
* [dive](https://github.com/wagoodman/dive) tool for exploring container layers

### Kubernetes Cluster

You will need a Kubernetes cluster to test the sample apps. The apps have been
tested with a docker desktop  Kubernetes. Other local desktop Kubernetes 
solutions such as minikube or kind will work but there might be slight 
differences in exposing network ports. Whatever choice of k8s you bring to 
the workshop we assume you are comfortable driving that k8s distribution. 
If you are new to Kubernetes and want the paved documented path install 
docker desktop and use the k8s that is built into it.

Pick your favourite laptop based k8s solution

* [docker desktop](https://www.docker.com/products/docker-desktop/)
* [minikube](https://minikube.sigs.k8s.io/docs/start/)
* [kind](https://kind.sigs.k8s.io/)
* [Rancher Desktop](https://rancherdesktop.io/)

#### Kubernetes tooling

* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [k9s](https://github.com/derailed/k9s) text gui for k8s
* [carvel](https://carvel.dev/) super useful set of clis for working kubernetes and containers
* [helm](https://helm.sh/) helm cli 

# Save the workshop wifi network

There is a lot of stuff that will be downloaded during the workshop, the conference wifi
network will likely be too so. You can save yourself a lot of time by doing the
following steps at home:

0. install the all the required software above, you don't want to be downloading and installing
   all the above software during the workshop, it will take too long and you will fall behind.
1. Checkout the code of this repo
2. run `./mvnw clean package` from the root of the repo to download all maven dependencies
3. Open the root of the repo in your IDE, the repo is a multi-module maven project so all
   the samples will be imported.
4. cd into  `containerize/buildpack` and run the
   command `./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=boot-buildpack:1`
   this will download a bunch of required base images that are quite big almost a 1GB in size.
5. from the repo root run the
   command `docker compose -f observability/tracing/docker-compose-all.yaml pull` to pull the
   required images for the observability demos

# Outline

Since 2014 Spring has been the leading framework for building microservices in
the Java ecosystem. A lot has changed over the past 8 years in both the Spring
ecosystem and wider cloud ecosystem. Kubernetes has matured and become widely
available on public and private clouds, giving us an industry standard
foundation for managing microservices. GraphQL and gRPC offer new possibilities
for exposing API endpoints. Service Meshes, Serverless functions give us many
more options for implementing and architecting microservices.

Unfortunately the internet is full of old blog posts and stackoverflow answers
recommending out of date approaches to building microservices in Spring.
This hands-on workshop is an up-to-date look at how to build microservice
based applications using the latest generation of Spring projects running
on Kubernetes.

The workshop consists of a series of sample applications which implement modern
microservice architecture patterns in Spring using various approaches so you
can select the approach that works best for your situation.

Technologies covered in the workshop

* Declarative clients (New feature in Spring 6)
* GraphQL (New feature in Spring 6)
* Spring Authorization Server (Customizable OIDC server)
* Spring Cloud Gateway
* Spring Cloud Sleuth
* Spring Cloud Function
* Observability with Micrometre 2 and Spring Framework 6
* How to effectively containerize Spring Boot apps
* How to effectively run Spring Boot apps on Kubernetes
* Understand Ahead-of-Time(AOT) and Native Images in Spring Boot 3.0

Architectural topics that we will discuss

* Service discovery
* Traffic routing
* API Gateways vs. Service Mesh
* Workload identity vs. User Identity
* Securing microservices
* Kubernetes native service discovery vs. Netflix Eureka
* Kubernetes native configuration vs. Spring Cloud Config
* GraphQL vs. REST
* Serverless Functions vs. Long Running Processes

Come learn how to use new capabilities introduced in Spring Framework 6.1,
Spring Boot 3.3, and Spring Cloud 2023, running on Kubernetes, to design and
implement modern microservices.
