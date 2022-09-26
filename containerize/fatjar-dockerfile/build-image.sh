#!/bin/bash
set -x
./mvnw package -DskipTests
docker build . -t boot-fatjar:1
docker images boot-*