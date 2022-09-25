#!/bin/bash
set -x
./mvnw package
docker build . -t boot-custom-layers:1
docker images boot-*