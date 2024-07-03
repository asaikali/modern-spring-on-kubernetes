# dockerfile-cds

Example showing how to package a Spring Boot into an optimized multi-layer container 
image using a Dockerfile and AppCDS for faster startup. On my laptop
this image starts in 1/2 the time as the image without CDS.

**Prerequisites** 

* [Java 17 JDK](https://adoptium.net/)
* [Docker](https://www.docker.com/products/docker-desktop) 
* [dive](https://github.com/wagoodman/dive) tool for exploring container layers 

**build and run the app** 

* build the app `./mvnw clean package` to produce the fat jar 
* build the container `docker build . -t boot-docker-cds:1` 
* check the size of the container `boot-docker-cds:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t boot-docker-cds:1`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`

**Explore the container layers**
* run the `cds-extract.sh` script
* inspect the `target/out`directory notice the directory layout 
* run the `cds-train.sh` script 
* notice the `target/out/app.jsa` file
* run the `cds-run.sh` notice it starts faster than normal

**Resources**

* [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/container-images.html#container-images.efficient-images.layering)
* [Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image)
* [Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image)
