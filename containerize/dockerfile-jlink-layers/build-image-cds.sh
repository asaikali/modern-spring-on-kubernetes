#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build .  -f Dockerfile-cds  -t boot-jlink-layers-cds:1
docker images boot-*
