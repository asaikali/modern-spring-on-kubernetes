#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build . -t boot-custom-layers:1
docker images boot-*