#!/bin/bash
set -x
docker run -p 8080:8080 -t boot-buildpack-cds:1
#docker run -p 8080:8080  --name boot-buildpack-cds-container -t boot-buildpack-cds:1
