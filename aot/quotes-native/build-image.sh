#!/bin/bash
set -x
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=quotes-native:1
docker images boot-*