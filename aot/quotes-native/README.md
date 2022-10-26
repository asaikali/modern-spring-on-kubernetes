# Quotes App - Native Image Example
Example shows how to build JIT(JVM) and Native Java images with Spring Native and GraalVM.

Instructions show you how to build/run both application and container images, using either `Gradle` or `Maven`.

### Prerequisites
* [Java 17 JDK](https://adoptium.net/)
* [GraalVM 22.2 - Java 17](https://www.graalvm.org/22.2/docs/getting-started/) - to build Native Java application images `or`
* [Liberica NIK 22.2 - Java 17](https://bell-sw.com/pages/downloads/native-image-kit/#) - alternate Native Java Image building tool based on open-source GraalVM 
* [Docker](https://www.docker.com/products/docker-desktop) 
* [dive](https://github.com/wagoodman/dive) tool for exploring container layers 
* [cURL](https://curl.se/docs/manpage.html) or [HTTPie](https://httpie.io/) - app testing

### Install GraalVM 
* [SDKMan - preferred method](https://sdkman.io/)
    * GraalVM 22.3 
        * `sdk install java 22.3.r17-grl` - select `Y` to set as default `or`
        * `sdk use java 22.3.r17-grl
        * `gu install native-image`
    * Liberica NIK 22.3: 
        * `sdk install java 22.3.r17.ea-nik`  - select `Y` to set as default `or`
        * `sdk use java 22.3.r17.ea-nik`
* [GraalVM one-line installer]
    * install script released with 22.3 on OCt 25, 2022
    * `bash <(curl -sL https://get.graalvm.org/jdk) graalvm-ce-java17-22.3.0`
* [Using Homebrew](https://github.com/graalvm/homebrew-tap)
* [From GraalVM Github repo](https://github.com/graalvm/graalvm-ce-builds/releases)

### Known issues
* `java.lang.management.ThreadInfo` is not exposed at this time in Spring Native, due to an [open issue](https://github.com/oracle/graal/issues/1039) in GraalVM - this can result in a failure of the Native Java application build with GraalVM

## **Build and Test with Gradle**
### App Images
**Build and test the JIT Application on a regular Java 17 JVM**
* build the app `./gradlew build`
* check the `build/libs` folder, observe the size of the `quotes-native-0.0.1-SNAPSHOT.jar` file
* run the app on the JVM `./gradlew bootRun`
* test the app using a browser `http://localhost:8080/`

**Build and test the Native Java Application**
* build the app `./gradlew clean nativeCompile`
* observe the significantly longer build time for the native image
* check the `build/native/nativeCompile` folder, observe the size of the `quotes-native` executable file. Note that the image is larger than the JIT image, but does not require the JRE for execution
* run the app on the JVM `./build/native/nativeCompile/quotes-native` or `./gradlew nativeRun`
* test the app using a browser `http://localhost:8080/`

### Container images
**Build and test the Containerized JIT Application using a regular Java 17 VM**
* build the JIT app and containerize with buildpacks `./gradlew bootBuildImage --imageName quotes-native:jit`
* **[alternatively]** you can download a pre-built Docker container `docker pull ghcr.io/ddobrin/quotes-native:jit`
* check the size of the container `docker images | grep quotes*`
* `dive` into the container to observe the container layers, including JRE, app classes and dependent libraries `dive quotes-native:jit`
* run the container `docker run -p 8080:8080 quotes-native:jit`
* test the app using a browser `http://localhost:8080/`

**Build and test the Containerized Native Java Application**
* build the Native Java app and containerize with buildpacks `./gradlew bootBuildImage -Pnative --imageName quotes-native:aot`
* **[alternatively]** you can download a pre-built Docker container `docker pull ghcr.io/ddobrin/quotes-native:aot`
* check the size of the container `docker images | grep quotes*`. Observe the significantly smaller size of the `quotes-native:aot` container
* `dive` into the container to observe that only the native image has been added `dive quotes-native:aot`
* run the container `docker run -p 8080:8080 quotes-native:aot`
* test the app using a browser `http://localhost:8080/`

## **Build and Test with Maven**

### App Images
**Build and test the JIT Application on a regular Java 17 JVM**
* build the app `./mvnw package`
* check the `target` folder, observe the size of the `quotes-native-0.0.1-SNAPSHOT.jar` file
* run the app on the JVM `./mvnw spring-boot:run`
* test the app using a browser `http://localhost:8080/`

**Build and test the Native Java Application**
* build the app `./mvnw native:compile -Pnative`
* observe the significantly longer build time for the native image
* check the `target` folder, observe the size of the `quotes-native` executable file. Note that the image is larger than the JIT image, but does not require the JRE for execution
* run the app on the JVM `./target/quotes-native`
* test the app using a browser `http://localhost:8080/`

### Container images
**Build and test the Containerized JIT Application using a regular Java 17 JVM**
* build the JIT app and containerize with buildpacks `./mvnw spring-boot:build-image -Dspring-boot:build-image.imageName=quotes-native-maven:jit`
* **[alternatively]** you can download a pre-built Docker container `docker pull ghcr.io/ddobrin/quotes-native-maven:jit`
* check the size of the container `docker images | grep quotes*`
* `dive` into the container to observe the container layers, including JRE, app classes and dependent libraries `dive quotes-native-maven:jit`
* run the container `docker run -p 8080:8080 quotes-native-maven:jit`
* test the app using a browser `http://localhost:8080/`

**Build and test the Containerized Native Java Application**
* build the Native Java app and containerize with buildpacks `./gradlew bootBuildImage -Pnative --imageName quotes-native-maven:aot -Pnative`
* **[alternatively]** you can download a pre-built Docker container `docker pull ghcr.io/ddobrin/quotes-native-maven:aot`
* check the size of the container `docker images | grep quotes*`. Observe the significantly smaller size of the `quotes-native-maven:aot` container
* `dive` into the container to observe that only the native image has been added `dive quotes-native-maven:aot`
* run the container `docker run -p 8080:8080 quotes-native-maven:aot`
* test the app using a browser `http://localhost:8080/`
