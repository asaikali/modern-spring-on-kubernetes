#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build . -t boot-docker-cds:1
docker images boot-*
