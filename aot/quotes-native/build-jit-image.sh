#!/bin/bash
set -x
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=quotes-native:jit
docker images boot-*
