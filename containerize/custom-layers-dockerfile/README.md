# custom-layers-dockerfile 

Example showing how to package a Spring Boot into a customized multi-layer
container image using a Dockerfile. For example, if you have corporate .jar 
files that you want to put into a dedicated company dependencies layer.

**build and run the app**

* build the app `./mvnw clean package` to produce the fat jar 
* build the container `docker build . -t boot-custom-layers:1` 
* check the size of the container `boot-custom-layers:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t boot-custom-layers:1`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`

**Explore the container layers**
* using the command line navigate to target folder `cd target`
* execute the command `java -Djarmode=layertools -jar custom-layers-dockerfile-0.0.1-SNAPSHOT.jar`
* execute the command `java -Djarmode=layertools -jar custom-layers-dockerfile-0.0.1-SNAPSHOT.jar list`
* create a temporary directory `t`
* navigate to the new directory `cd t`
* execute the command `java -Djarmode=layertools -jar ../custom-layers-dockerfile-0.0.1-SNAPSHOT.jar extract`
* using your file browser navigate into the `target/t` folder and check what is in those directories
  ** notice that it has an extra directory `company-dependencies`
* examine the contents of the `Dockerfile` notice 
  * it is a multistage dockerfile  
  * the first stage extracts the layers in the jar file into a directory 
  * the second stage create the layers in the image
  * notice the instruction for add the `company-dependencies` layer 
* explore the layers in the container using `dive boot-custom-layers:1` find the app.jar 

**Explore the Spring Boot custom layers**
* inspect the `spring-boot-maven-plugin` definition in `pom.xml` notice the reference to 
  `src/layers.xml`
* inspect the `src/layers.xml` file to see the custom layer definition take notice of 
  ** the `company-dependencies` section which is used to select a set of .jar file 
     dependencies from the maven classpath to add to that layer 
  ** we added the postgres jdbc driver jar to the simulated company-dependencies to show 
     how it works. 
  ** the layers.xml works with gradle and maven 

**Resources**
 
* [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/container-images.html#container-images.efficient-images.layering) 
* [Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image)
* [Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image)
