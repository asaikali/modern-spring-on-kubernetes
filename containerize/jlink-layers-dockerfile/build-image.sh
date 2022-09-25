#!/bin/bash
set -x
#./mvnw package
docker build . -t boot-jlink-layers:1
docker images boot-*