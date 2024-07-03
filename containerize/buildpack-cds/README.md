# buildpack-cds

Example showing how to containerize a java application using buildpacks without 
having to write a Dockerfile. 

**Prerequisites** 

* [Java 17 JDK](https://adoptium.net/)
* [Docker](https://www.docker.com/products/docker-desktop) 
* [dive](https://github.com/wagoodman/dive) tool for exploring container layers 

**Try it out** 

* notice that this project does not have a `Dockerfile` it is not required 
* execute the command `./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=boot-buildpack-cds:1`
* read the console output from the `spring-boot-plugin` to see what the buildpack is doing
* check the size of the container `boot-buildpack-cds:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t boot-buildpack-cds:1`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`
* explore the layers in the container using `dive boot-buildpack-cds:1` find the layers that have app dependencies and code
* compare the amount of time it takes to start the CDS based image vs. the regular buildpack image on my machine CDS impage 1/2 startup time.

**Resources**
 
* [Spring to Image SpringOne 2020 Talk](https://www.youtube.com/watch?v=44n_MtsggnI)
* [buildpacks.io](https://buildpacks.io/)
* [Maven Plugin](https://docs.spring.io/spring-boot/docs/2.5.0/maven-plugin/reference/htmlsingle/#build-image)
* [Gradle Plugin](https://docs.spring.io/spring-boot/docs/2.5.0/gradle-plugin/reference/htmlsingle/#build-image)
