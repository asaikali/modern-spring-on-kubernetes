# build-test-aot-native
Dive into AOT, then build Native Spring Images with Spring Boot 3.0

This repository provides a basic web application using Spring Boot 3 that can be built as a native image using GraalVM.
It showcases how reflection, serialization, proxying and resource loading can be configured using `RuntimeHints`.

This work expands on the sample built by [Stephane Nicoll](https://github.com/snicoll/demo-aot-native)

### Prerequisites
* [Java 17 JDK](https://adoptium.net/)
* [GraalVM 22.2 - Java 17](https://www.graalvm.org/22.2/docs/getting-started/) - to build Native Java application images `or`
* [Liberica NIK 22.2 - Java 17](https://bell-sw.com/pages/downloads/native-image-kit/#) - alternate Native Java Image building tool based on open-source GraalVM
* [cURL](https://curl.se/docs/manpage.html) or [HTTPie](https://httpie.io/) - app testing

### Install GraalVM
* [SDKMan - preferred method](https://sdkman.io/)
    * GraalVM 22.2: `sdk install java 22.2.0.r17-grl` or
    * Liberica NIK 22.2: `sdk install java 22.2.r17-nik`
* [Using Homebrew](https://github.com/graalvm/homebrew-tap)
* [From GraalVM Github repo](https://github.com/graalvm/graalvm-ce-builds/releases)

### Test commands for the Application
The following test commands allow you to test the JIT(JVM) and Native Java applications. The behaviour must be idempotent.
* Bean creation: `http :8080/helo mode==bean`
* Reflection: `http :8080/hello mode==reflection`
* Serialization: `http :8080/hello mode==serialization`
* Resource: `http :8080/hello mode==resource`
* Dynamic proxy: `http :8080/hello mode==proxy`

## How to run the workshop
* [Workshop Tasks using Gradle](#workshop-tasks-with-gradle)
* [Workshop Tasks using Maven](#workshop-tasks-with-maven)

# Workshop tasks with Gradle

## Dive into AOT
**Analyze generated AOT artifacts**
* check the available Gradle tasks: `./gradlew tasks --all`
* generate the AOT resources and observe whether they match dynamically registered `RuntimeHints` in `BuildTestAotNativeRuntimeHints`
  * `RuntimeHints` API helps you contribute hints for runtime reflection, resources, serialization and proxies with GraalVM native. You can contribute hints after encountering a failure at runtime for a native image, however you can also pro-actively register runtime hints before even building a native image.
* execute `./gradlew clean processAot`
* the Gradle output can be found in the `/build` subfolder
* inspect 
  * the `build/generated/aotResources/META-INF/native-image/com.example/build-test-aot-native` folder:
    * `reflect-config.json` must contain the classes and methods registered for reflection. Search for the `hello` keyword in the file
    * `resource-config.json` must contain the entry for the `hello.txt` file: `{"pattern": "\\Qhello.txt\\E"}`
    * `serialization-config.json` must contain the registration entries for ArrayList, Long and Number classes:`[{"name": "java.util.ArrayList"},{"name": "java.lang.Long"},{"name": "java.lang.Number"}]`
    * `proxy-config.json` must contain the registration entry for the proxied Map: `"interfaces": ["java.util.Map"]`
  * the generated Bean definitions in the `generated/aotSources/com/example/aot`, in the `BuildTestAotNativeConfiguration__BeanDefinitions.java` class
* build and run the application on the JVM, then test with the test commands listed above `./gradlew clean bootRun`; observe the short build time

## Build Native App Image and Native Tests
**Build a Native Image for the application**
* to build the native app with Gradle, you need to make sure that the `org.graalvm.buildtools.native` plugin is enabled in the `build.gradle` file
  * ```
    plugins {
	id 'org.graalvm.buildtools.native' version '0.9.14'
	id 'java'}
* build the native app with `/gradlew clean nativeBuild`; observe the longer build time
* observe `/build/native/nativeCompile` folder, and the native image `build-test-aot-native`. Note that the image is larger than the JIT image, but does not require the JRE for execution
* run the native app `./build/native/nativeCompile/build-test-aot-native` and test with the test commands listed above
* all tests must be successful

**Build Native Tests for the application**
* the application has tests for each runtime hint used throughout the app
* to run the tests on the JVM using Gradle, execute `./gradlew clean test` and observe all successful tests
* you can run your unit tests in a native image to verify that your application and its dependency work as expected
* to run your tests in a native image with Gradle, you need to make sure that the `org.graalvm.buildtools.native` plugin is enabled; see above
* build the native tests `./gradlew clean nativeTest` and observe that all tests are successful
* you can re-run the tests by executing the `build-test-aot-native` native tests located in `/build/native/nativeTestCompile` - `./build/native/nativeTestCompile/build-test-aot-native-tests`

## Observe a native image failure
* runtime hints can be used to optimize the application runtime. It can be hard to find out about the required hints without compiling an app to native and seeing it fail
* let's simulate a missing hint and addressing it by writing a proper test for it
* run the native tests you have built in the previous test `./build/native/nativeTestCompile/build-test-aot-native-tests`
* observe that the test with serialization has been successful 
  * `com.example.aot.BuildTestAotNativeControllerTests > helloWithSerializationMode() SUCCESSFUL
* open the `BuildTestAotNativeRuntimeHints` class and comment out one of the runtime hints for serialization
  * `// hints.serialization().registerType(java.util.ArrayList.class);`
* run the `processSAot` task to generate AOT-optimized source code for the app - `./gradlew clean processAot`
* note that the `serialization-config.json` config file in `build/generated/aotResources/META-INF/native-image/com.example/build-test-aot-native`does not contain the `ArrayList` anymore
* build the JIT tests and run them on the JVM: ` ./gradlew clean test`. Note that all tests are successful
* build the Native Tests `./gradlew clean nativeTest` and observe that the build fails, as expected, due to ArrayList not being available at runtime in the nativeimage
  * `java.util.ArrayList; no valid constructor
  * `Failures (1):` 
    * `JUnit Jupiter:BuildTestAotNativeControllerTests:helloWithSerializationMode()`
    * `MethodSource [className = 'com.example.aot.BuildTestAotNativeControllerTests', methodName = 'helloWithSerializationMode', methodParameterTypes = '']`
    * `=> java.lang.AssertionError: JSON path "message" expected:<Serialization: Native Fibonacci generation> but was:<Could not open input stream to read object>`

### Reference Documentation
For further reference, please consider the following sections:
* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.0-M5/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.0-M5/gradle-plugin/reference/html/#build-image)

### Additional Links
These additional references should also help you:
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

# Workshop tasks with Maven