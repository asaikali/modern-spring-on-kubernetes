# jvm-settings  

Example showing best practices for configuring JVM settings RAM, and CPU when running in a 
container. 

**build and run the app** 

* build the app `./mvnw clean package` to produce the fat jar 
* build the container `docker build . -t jvm-settings:1` 
* check the size of the container `jvm-settings:1` using `docker images` 
* run the container `docker run -p 8080:8080 -t jvm-settings:1`
* notice the first line of output it start with `Picked up JAVA_TOOL_OPTIONS: `
* inspect the `Dockerfile` notice the `ENV` command to set a default value for `JAVA_TOOL_OPTIONS`
* test the app using a browser `http://localhost:8080/`
* terminate the container using `Ctrl+C` or `docker kill`
* override the memory settings before launching the container 
  `docker run -e JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75" -p 8080:8080 -t jvm-settings:1`
* notice the first line of output it start with `Picked up JAVA_TOOL_OPTIONS: ` has the new 
  percentage you set. 
* terminate the container using `Ctrl+C` or `docker kill`
* try setting JVM args for a container from a dockerfile that did not set the `JAVA_TOOL_OPTIONS`
  you can use the `boot-fatjar:1` use the command 
    `docker run -e JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75" -p 8080:8080 -t jvm-settings:1`
* notice tha the jvm picked the options, there is no need to add an `ENV` statement to set 
  the `JAVA_TOOL_OPTIONS` you can set it before the container runs. This is the recommended 
  way set JVM args on Kubernetes. 

**Resources**
 
* [JAVA_TOOL_OPTIONS env var](https://docs.oracle.com/en/java/javase/21/troubleshoot/environment-variables-and-system-properties.html#GUID-BE6E7B7F-A4BE-45C0-9078-AA8A66754B97)
