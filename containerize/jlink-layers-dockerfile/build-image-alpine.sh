#!/bin/bash
set -x
#./mvnw package
docker build . -f Dockerfile-alpine -t boot-jlink-layers-alpine:1
docker images boot-*