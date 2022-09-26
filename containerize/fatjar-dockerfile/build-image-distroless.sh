#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build . -f Dockerfile-distroless -t boot-fatjar-distroless:1
docker images boot-*