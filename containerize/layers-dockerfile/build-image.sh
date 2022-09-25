#!/bin/bash
set -x
./mvnw package
docker build . -t boot-layers:1
docker images boot-*