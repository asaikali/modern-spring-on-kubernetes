#!/bin/bash
set -x
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=boot-buildpack:1
docker images boot-*