# modern-spring-on-kubernetes

Modern Spring Based Microservices on Kubernetes

Example applications show to use Spring Boot on Kubernetes. There are two types
of sample application in this repo. Samples showing various approaches for
containerizing Spring applications in the `containerize` folder.
Samples showing features for running spring applications on Kubernetes in `run`
folder. Each sample application has a `readme.md` that explains how to run the
sample and points out interesting things to look at.

## Software Prerequisites

* [Java 17 JDK](https://adoptium.net/)
* Favourite Java IDE [Eclipse Spring Tool Suite](https://spring.io/tools) or [IntelliJ](https://www.jetbrains.com/idea/download)
* [Docker](https://www.docker.com/products/docker-desktop)
* [dive](https://github.com/wagoodman/dive) tool for exploring container layers
* [k9s](https://github.com/derailed/k9s) text gui for k8s
* [Kubernetes](https://kubernetes.io/) sample tested with Docker Desktop K8s. If you have a different k8s install you must know how to expose the app in k8s to your machine machine if you don't just use docker desktop k8s to run these demos.


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
* How to effectively containerized spring boot apps
* How to effectively run spring boot apps on Kubernetes
* High level overview of Spring Native 


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
Spring Boot 3, and Spring Cloud 2022 running on Kubernetes to design and 
implement modern microservices. These projects are due to GA in the Nov / Dec
2022 timeframe, so we will be using the latest milestones or release candidates 
or milestones available