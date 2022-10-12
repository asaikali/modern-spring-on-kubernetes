#!/bin/bash
set -x
./gradlew bootBuildImage --imageName quotes-native:jit
# ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=quotes-native:1
docker images boot-*