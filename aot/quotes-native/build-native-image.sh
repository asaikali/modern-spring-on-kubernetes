#!/bin/bash
set -x
./mvnw -Pnative spring-boot:build-image -Dspring-boot.build-image.imageName=quotes-native:aot
docker images boot-*
