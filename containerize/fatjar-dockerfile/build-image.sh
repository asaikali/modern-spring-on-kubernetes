#!/bin/bash
set -x
./mvnw package
docker build . -t boot-fatjar:1
docker images boot-*