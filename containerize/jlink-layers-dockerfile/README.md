# jlink-layers-dockerfile 

Example showing how to package a Spring Boot into an optimized multi-layer 
container image using a Dockerfile, while also creating a minimal custom JRE
that only containers the modules needed by the application. 

**build and run the app** 

* build the app `./mvnw clean package` to produce the fat jar 
* build the container `docker build . -t boot-jlink-layers:1` 
* check the size of the container `boot-jlink-layers:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t boot-jlink-layers:1`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`

**Explore the container layers**
* using the command line navigate to target folder `cd target`
* execute the command `java -Djarmode=layertools -jar jlink-layers-dockerfile-0.0.1-SNAPSHOT.jar`
* execute the command `java -Djarmode=layertools -jar jlink-layers-dockerfile-0.0.1-SNAPSHOT.jar list`
* create a temporary directory `mkdir t`
* navigate to the new directory `cd t`
* execute the command `java -Djarmode=layertools -jar ../jlink-layers-dockerfile-0.0.1-SNAPSHOT.jar extract`
* using your file browser navigate into the `target/t` folder and check what is in those directories
* examine the contents of the `Dockerfile` notice 
  * it is a multistage dockerfile  
  * the first stage extracts the layers in the jar file into a directory 
  * the second stage create the layers in the image
* explore the layers in the container using `dive boot-jlink-layers:1` find the app.jar 

**Resources**

* [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/container-images.html#container-images.efficient-images.layering)
* [Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image)
* [Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image)