# Containerize 

There is a general impressien that Java is big and heavyweight, this impression
is wrong. The table below shows the container image sizes for a variety of 
popular open source project. Java does pretty well on these measures. 

If you use jlink and boot layers as shown in the `jre` and `dockerfile-jlink-layers` 
you can get a JRE image for running spring boot apps in 68MB and 
Spring MVC + JPA app in 123MB image.

In a real world Kubernetes will cache image layers on the worker nodes, this means
that if your apps are using `eclipse-temurin:21-jre` at 270MB that image will 
already be on the worker node as you frequently depoly your apps. Basically,
the image size is not a problem in the java world.

## Popular Image Sizes 
You can run the `sizes.sh` script to re-generate the table below. 

```text
./sizes.sh

Here is a list of popular open-source projects and their official images sizes
on July 06 2024

+------------+---------------------------------------------------------+
| Size (MB)  | Operating Systems                                       |
+------------+---------------------------------------------------------+
| 8          | alpine:3                                                |
| 69         | ubuntu:22.04                                            |
| 139        | debian:12                                               |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Proxies and Load Balancers                              |
+------------+---------------------------------------------------------+
| 126        | haproxy:3.0                                             |
| 145        | envoyproxy/envoy:v1.30-latest                           |
| 169        | traefik:3.0                                             |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Web Servers                                             |
+------------+---------------------------------------------------------+
| 178        | httpd:latest                                            |
| 193        | nginx:latest                                            |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Recommended Runtimes                                    |
+------------+---------------------------------------------------------+
| 222        | mcr.microsoft.com/dotnet/runtime:8.0                    |
| 270        | eclipse-temurin:21-jre                                  |
| 995        | ruby:3                                                  |
| 1044       | python:3                                                |
| 1116       | node:20                                                 |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Alpine Runtimes                                         |
+------------+---------------------------------------------------------+
| 63         | python:3-alpine                                         |
| 87         | ruby:3-alpine                                           |
| 132        | node:20-alpine                                          |
| 188        | eclipse-temurin:21-jre-alpine                           |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Slim Runtimes                                           |
+------------+---------------------------------------------------------+
| 153        | bellsoft/liberica-runtime-container:jre-21-slim-glibc   |
| 155        | python:3-slim                                           |
| 206        | ruby:3-slim                                             |
| 219        | node:20-slim                                            |
+------------+---------------------------------------------------------+

+------------+---------------------------------------------------------+
| Size (MB)  | Data Services                                           |
+------------+---------------------------------------------------------+
| 139        | redis:latest                                            |
| 206        | rabbitmq:3                                              |
| 434        | mariadb:11                                              |
| 453        | postgres:16                                             |
| 761        | mongo:7                                                 |
| 829        | confluentinc/cp-kafka:7.6.1                             |
+------------+---------------------------------------------------------+
+------------+---------------------------------------------------------+
| Size (MB)  | Java Runtimes                                           |
+------------+---------------------------------------------------------+
| 153        | bellsoft/liberica-runtime-container:jre-21-slim-glibc   |
| 188        | eclipse-temurin:21-jre-alpine                           |
| 270        | eclipse-temurin:21-jre                                  |
| 314        | amazoncorretto:21-alpine                                |
| 332        | mcr.microsoft.com/openjdk/jdk:21-distroless             |
| 429        | eclipse-temurin:21-jdk                                  |
| 346        | eclipse-temurin:21-jdk-alpine                           |
| 438        | mcr.microsoft.com/openjdk/jdk:21-ubuntu                 |
+------------+---------------------------------------------------------+
```
