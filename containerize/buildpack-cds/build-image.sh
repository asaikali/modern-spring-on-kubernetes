#!/bin/bash
set -x
./mvnw clean spring-boot:build-image #-Dspring-boot.build-image.imageName=boot-buildpack-cds:1
docker images boot-*
docker inspect boot-buildpack-cds:1 --format '{{.Os}}/{{.Architecture}}'
