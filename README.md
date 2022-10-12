# modern-spring-on-kubernetes

**Modern Spring Based Microservices on Kubernetes**

Example applications show to use Spring Boot on Kubernetes. There are multiple types
of sample applications in this repo. Samples showing various approaches for
containerizing Spring applications in the `containerize` folder.
Samples showing features for running Spring applications on Kubernetes in `run`
folder. Samples showing features for AOT in the `aot` folder. Each sample application has a `README.md` that explains how to run the
sample and points out interesting things to look at.

## Software Prerequisites

### Java development tooling 
* [Java 17 JDK](https://adoptium.net/)
* [Java 17 GraalVM](https://www.graalvm.org/22.2/docs/getting-started/) or [Java 17 Liberica NIK 22.2](https://bell-sw.com/pages/downloads/native-image-kit/#) (Spring Native)
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

You will need a Kubernetes cluster running on your laptop to test the sample
apps. The workshop samples have been tested with a kind based local cluster. 
While Minikube and docker desktop kubernetes can be used to run the apps, the
instructions and scripts assume kind is available so please make suer you have
kind installed.

* [kind](https://kind.sigs.k8s.io/docs/user/quick-start/) local kubernetes cluster

is to use the cluster that is built into docker desktop 
that is what the samples have been tested with. 

### Kubernetes tooling
* [Kubernetes](https://kubernetes.io/) sample tested with Docker Desktop K8s. If you have a different k8s install you must know how to expose the app in k8s to your machine machine if you don't just use docker desktop k8s to run these demos.
* [minikube](https://minikube.sigs.k8s.io/docs/start/)
* [kubectl](https://code.visualstudio.com/)
* [k9s](https://github.com/derailed/k9s) text gui for k8s
* [carvel](https://carvel.dev/) super useful set of clis for working kubernetes and containers 




# Outline 

Since 2014 Spring has been the leading framework for building microservices in
the Java ecosystem.  A lot has changed over the past 8 years in both the Spring 
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
* Kubernetes native configuration  vs. Spring Cloud Config
* GraphQL vs. REST
* Serverless Functions vs. Long Running Processes

Come learn how to use new capabilities introduced in Spring Framework 6, 
Spring Boot 3, and Spring Cloud 2022, running on Kubernetes, to design and 
implement modern microservices. 

These projects are due to GA in the Nov / Dec
2022 timeframe, so we will be using the latest milestones or release candidates available.