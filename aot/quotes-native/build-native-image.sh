#!/bin/bash
set -x
 ./gradlew bootBuildImage -Pnative --imageName quotes-native:aot
# ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=quotes-native:1
docker images boot-*