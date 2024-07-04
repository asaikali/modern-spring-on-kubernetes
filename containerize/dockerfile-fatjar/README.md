# dockerfile-fatjar 

Example showing how to bundle a Spring Boot fat jar into a container image using 
a Dockerfile.

**Try it out** 

* build the app `./mvnw clean package` to produce the fat jar 
* check the `target/` folder, observe the size of the `fatjar-dockerfile-0.0.1-SNAPSHOT.jar` file
* build the container `docker build . -t boot-fatjar:1` 
* check the size of the container `boot-fatjar:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t boot-fatjar:1`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`
* examine the contents of the `Dockerfile` notice the path to `app.jar` in the container 
* explore the layers in the container using `dive boot-fatjar:1` find the app.jar 
