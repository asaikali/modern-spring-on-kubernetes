docker build . -f Dockerfile-alpine -t boot-jre-alpine:1
docker build . -f Dockerfile-ubuntu -t boot-jre-ubuntu:1
docker images boot-*
