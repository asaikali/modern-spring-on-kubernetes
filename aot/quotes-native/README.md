# Native Image 

## Docker builds

App images
```
# build and run
./mvnw clean package
./mvnw spring-boot:run

# size
ls -lart target/
-rw-r--r--   1 ddobrin  primarygroup  47166571  5 Oct 09:42 quotes-native-0.0.1-SNAPSHOT.jar

# build and run
./mvnw clean package -Pnative


# size
s -lart target/quotes*
-rw-r--r--  1 ddobrin  primarygroup   47858557  5 Oct 11:27 target/quotes-native-0.0.1-SNAPSHOT.jar
-rwxr-xr-x  1 ddobrin  primarygroup  139284848  5 Oct 11:33 target/quotes-native
```
Docker images
```
./gradlew bootBuildImage --imageName quotes-native:jit
dive quotes-native:jit

./gradlew bootBuildImage -Pnative --imageName quotes-native:aot
dive quotes-native:aot

docker images | grep quotes*
quotes-native aot                                        115f81c71155   42 years ago    60.8MB
quotes-native latest                                     c6e4d682be1c   42 years ago    58.6MB
quotes-native jit                                        ea0f81a468e7   42 years ago    304MB
```


## Gradle builds

App images
```
# build and run
./gradlew build
./gradlew bootRun

# size
ls -lart build/libs/
-rw-r--r--   1 ddobrin  primarygroup  47166571  5 Oct 09:42 quotes-native-0.0.1-SNAPSHOT.jar

# build and run
./gradlew nativeBuild
build/native/nativeCompile/quotes-native

# size
ls -last build/native/nativeCompile
263328 -rwxr-xr-x  1 ddobrin  primarygroup  134823336  5 Oct 10:16 quotes-native
```
Docker images
```
./gradlew bootBuildImage --imageName quotes-native:jit
dive quotes-native:jit

./gradlew bootBuildImage -Pnative --imageName quotes-native:aot
dive quotes-native:aot

docker images | grep quotes*
quotes-native aot                                        115f81c71155   42 years ago    60.8MB
quotes-native latest                                     c6e4d682be1c   42 years ago    58.6MB
quotes-native jit                                        ea0f81a468e7   42 years ago    304MB
```