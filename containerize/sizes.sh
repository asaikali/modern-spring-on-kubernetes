#!/bin/bash

echo """
Here is a list of popular open-source projects and their official images sizes
"""

# Function to get image size in MB
get_image_size() {
  local image=$1
  local platform=$2
  if [ -n "$platform" ]; then
    docker pull --platform $platform $image > /dev/null
  else
    docker pull $image > /dev/null
  fi
  size=$(docker images --format "{{.Size}}" ${image})
  # Convert size to MB and round to the nearest whole number
  size_mb=$(echo $size | awk '
    /MB$/ { sub(/MB/, "", $1); print int($1) }
    /GB$/ { sub(/GB/, "", $1); print int($1 * 1024) }
    /kB$/ { sub(/kB/, "", $1); print int($1 / 1024) }
    /B$/  { sub(/B/, "", $1); print int($1 / 1048576) }
  ')
  printf "| %-10s | %-55s |\n" "$size_mb" "$image"
}

# Function to print table header with category
print_table_header() {
  local category=$1
  printf "+------------+---------------------------------------------------------+\n"
  printf "| %-10s | %-55s |\n" "Size (MB)" "$category"
  printf "+------------+---------------------------------------------------------+\n"
}

# Function to print table footer
print_table_footer() {
  printf "+------------+---------------------------------------------------------+\n"
}

# Operating Systems
print_table_header "Operating Systems"
get_image_size "alpine:3"
get_image_size "ubuntu:22.04"
get_image_size "debian:12"
print_table_footer
echo

# Proxies and Load Balancers
print_table_header "Proxies and Load Balancers"
get_image_size "haproxy:3.0"
get_image_size "envoyproxy/envoy:v1.30-latest"
get_image_size "traefik:3.0"
print_table_footer
echo

# Web Servers
print_table_header "Web Servers"
get_image_size "httpd:latest"
get_image_size "nginx:latest"
print_table_footer
echo

# Recommended Runtimes
print_table_header "Recommended Runtimes"
get_image_size "mcr.microsoft.com/dotnet/runtime:8.0"
get_image_size "eclipse-temurin:21-jre"
get_image_size "ruby:3"
get_image_size "python:3"
get_image_size "node:20"
print_table_footer
echo

# Alpine Runtimes
print_table_header "Alpine Runtimes"
get_image_size "python:3-alpine"
get_image_size "ruby:3-alpine"
get_image_size "node:20-alpine"
get_image_size "eclipse-temurin:21-jre-alpine"
print_table_footer
echo

# Slim Runtimes
print_table_header "Slim Runtimes"
get_image_size "bellsoft/liberica-runtime-container:jre-21-slim-glibc" "linux/amd64"
get_image_size "python:3-slim"
get_image_size "ruby:3-slim"
get_image_size "node:20-slim"
print_table_footer
echo

# Data Services
print_table_header "Data Services "
get_image_size "redis:latest"
get_image_size "rabbitmq:3"
get_image_size "mariadb:11"
get_image_size "postgres:16"
get_image_size "mongo:7"
get_image_size "confluentinc/cp-kafka:7.6.1"
print_table_footer
echo

