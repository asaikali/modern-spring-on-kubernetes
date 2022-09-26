#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build . -t boot-layers:1
docker images boot-*