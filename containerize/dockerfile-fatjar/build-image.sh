#!/bin/bash
set -x
./mvnw clean package -DskipTests
docker build . -t boot-fatjar:1
docker images boot-*