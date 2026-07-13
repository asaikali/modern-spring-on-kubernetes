# build-test-aot-native
Dive into Ahead-of-Time(AOT), then build Native Spring Application Images with Spring Boot 3.0

This repository provides a basic web application using Spring Boot 3 that can be built as a native image using GraalVM.
It showcases how reflection, serialization, proxying and resource loading can be configured using `RuntimeHints`.

This work expands on the sample built by [Stephane Nicoll](https://github.com/snicoll/demo-aot-native)

### Install GraalVM
* [SDKMan - preferred method](https://sdkman.io/)
    * GraalVM 21
        * `sdk install java 21.0.1-graalce ` - select `Y` to set as default `or`
        * `sdk use 21.0.1-graalce`
        * `gu install native-image`
* [GraalVM 22.3 one-line installer]
    * install script released with 22.3 on OCt 25, 2022
    * `bash <(curl -sL https://get.graalvm.org/jdk) graalvm-ce-java17-22.3.0`
* [Using Homebrew](https://github.com/graalvm/homebrew-tap)
* [From GraalVM Github repo](https://github.com/graalvm/graalvm-ce-builds/releases)

### Test commands for the Application
The following test commands allow you to test the JIT(JVM) and Native Java applications. The behaviour must be idempotent.

[HTTPie](https://httpie.io/)
* Bean creation: `http :8080/hello mode==bean`
* Reflection: `http :8080/hello mode==reflection`
* Serialization: `http :8080/hello mode==serialization`
* Resource: `http :8080/hello mode==resource`
* Dynamic proxy: `http :8080/hello mode==proxy`

[cURL](https://curl.se/)
* Bean creation: `curl 'localhost:8080/hello?mode=bean'`
* Reflection: `curl 'localhost:8080/hello?mode=reflection'`
* Serialization: `curl 'localhost:8080/hello?mode=serialization'`
* Resource: ` curl 'localhost:8080/hello?mode=resource'`
* Dynamic proxy: `curl 'localhost:8080/hello?mode=proxy'`

# Workshop tasks w/Maven

## Dive into AOT
**Analyze generated AOT artifacts**
* generate the AOT resources and observe whether they match dynamically registered `RuntimeHints` in `BuildTestAotNativeRuntimeHints`
  * `RuntimeHints` API helps you contribute hints for runtime reflection, resources, serialization and proxies with GraalVM native. You can contribute hints after encountering a failure at runtime for a native image, however you can also pro-actively register runtime hints before even building a native image.
* execute `./mvnw clean compile spring-boot:process-aot`
* the generated output can be found in the `target/spring-aot` subfolder
* inspect at random
  * the `target/spring-aot/main/resources/META-INF/native-image/com.example/build-test-aot-native` folder:
    * `reflect-config.json` must contain the classes and methods registered for reflection. Search for the `hello` keyword in the file
    * `resource-config.json` must contain the entry for the `hello.txt` and `app-resources.properties` files: `{"pattern": "\\Qhello.txt\\E"}` and the `{"pattern": "\\Qapp-resources.properties\\E"}`
    * `serialization-config.json` must contain the registration entries for ArrayList, Long and Number classes:`[{"name": "java.util.ArrayList"},{"name": "java.lang.Long"},{"name": "java.lang.Number"}]`
    * `proxy-config.json` must contain the registration entry for the proxied Map: `"interfaces": ["java.util.Map"]`
  * the generated Bean definitions in `target/spring-aot/main/sources/com/example/aot`, in the `BuildTestAotNativeConfiguration__BeanDefinitions.java` class
* build and run the application on the JVM, then test with the test commands listed above `./mvnw spring-boot:run`
* observe the short build time

## Build Native App Image and Native Tests
**Build a Native Image for the application**
* native builds use the GraalVM Native Build Tools integration that comes with the `native` profile of `spring-boot-starter-parent` (a GraalVM JDK must be your active Java)
* build the native app with `./mvnw -Pnative clean native:compile`; observe the longer build time
* observe the `target` folder, and the native executable `build-test-aot-native`. Note that the image is larger than the JIT image, but does not require the JRE for execution
* run the native app `./target/build-test-aot-native` and test with the test commands listed above
* all tests must be successful
* observe the longer build time

**Build Native Tests for the application**
* the application has tests for each runtime hint used throughout the app
* to run the tests on the JVM using Maven, execute `./mvnw clean test` and observe all successful tests
* then run your unit tests in a native test image to verify that your application and its dependency work as expected
* build and run the native tests with `./mvnw -PnativeTest clean test` and observe that all tests are successful (the native test executable is generated under `target/`)

## Identify missing runtime hints **without** building a native image!!
* runtime hints can be used to optimize the application runtime. It can be hard to find out about the required hints without compiling an app to native and seeing it fail
  * the new `spring-core-test` module ships a Java agent that will help you with this aspect
  * the agent records all method invocations that are related to such hints and helps you to assert that a given `RuntimeHints` instance
covers all recorded invocations
* open the test class `/src/test/com/example/aot/BuildTestAotNativeJavaAgentRuntimeHintsTests`
  * note the `@EnabledIfRuntimeHintsAgent` annotation - it conditions the execution of tests only if the agent is loaded in the current JVM
  * the Java Agent will be loaded only if the annotation is present, to prevent test pollution
  * observe the `shouldRegisterReflectionHints()` test method
* run the test with `./mvnw -Pruntimehints test` and observe that it is successful
* comment out the added hint `runtimeHints.resources().registerPattern("app-resources.properties");` and run the tests again
* observe that the missing hint is flagged and you have not bean required to build the `native` image to find out
* question: the hint was recorded and tested in the unit test. **Why?**
  * implementations of the `RuntimeHintsRegistrar` are not invoked when running unit tests, as the full server is not loaded
  * this advanced mode of registering and testing hints allows us to test/identify/register hints for code snippets which might occur in multiple places throughout a codebase, for fast feedback without the need of building a native image.

## Observe native image failures - fix with proper unit tests!!
* unit tests allow us to identify missing hints
* let's simulate a missing hint and addressing it by writing a proper test for it
* run the native tests you have built in the previous task `./mvnw -PnativeTest test`
* observe that the test with serialization has been successful 
  * `com.example.aot.BuildTestAotNativeControllerTests > helloWithSerializationMode() SUCCESSFUL
* open the `BuildTestAotNativeRuntimeHints` class and comment out one of the runtime hints for serialization
  * `// hints.serialization().registerType(java.util.ArrayList.class);`
* run the AOT processing again to generate AOT-optimized source code for the app - `./mvnw clean compile spring-boot:process-aot`
* note that the `serialization-config.json` config file in `target/spring-aot/main/resources/META-INF/native-image/com.example/build-test-aot-native` does not contain the `ArrayList` anymore
* build the JIT tests and run them on the JVM: `./mvnw clean test`. Note that all tests are successful
* build the Native Tests `./mvnw -PnativeTest clean test` and observe that the build fails, as expected, due to ArrayList not being available at runtime in the native image
  * ...
  * java.util.ArrayList; no valid constructor
  * `Failures (1):` 
    * `JUnit Jupiter:BuildTestAotNativeControllerTests:helloWithSerializationMode()`
    * `MethodSource [className = 'com.example.aot.BuildTestAotNativeControllerTests', methodName = 'helloWithSerializationMode', methodParameterTypes = '']`
    * `=> java.lang.AssertionError: JSON path "message" expected:<Serialization: Native Fibonacci generation> but was:<Could not open input stream to read object>`
  * ...

## Reference Documentation
For further reference, please consider the following sections:
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/maven-plugin/)
* [Create an OCI image](https://docs.spring.io/spring-boot/maven-plugin/build-image.html)
* [GraalVM Native Build Tools](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html)
