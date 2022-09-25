#!/bin/bash

set -eo pipefail

#
# Define usage and error functions
#
readonly PROFILE_NAME="modern-spring"
readonly BOLD=$(tput bold)
readonly NORMAL=$(tput sgr0)
readonly USAGE="

Starts and stops a minikube cluster for use during the workshop.
The cluster is started as profile called ${PROFILE_NAME}

Usage: $(basename $0) [command]

Commands:
  start   - starts a minikube cluster called ${PROFILE_NAME}
  stop    - stops the minikube cluster called ${PROFILE_NAME}
  delete  - delete minikube cluster called ${PROFILE_NAME}
  restart - restarts the minikube cluster called ${PROFILE_NAME}

${BOLD}Access minikube docker daemon: ${NORMAL}

You can point docker-cli at docker daemon in minikube vm by running
the command 'eval \$(minikube -p ${PROFILE_NAME}) docker-env)'

Examples:
  $(basename $0) start
"

function usage() {
    echo "${USAGE}"  1>&2
}


function start_cluster() {
  set -x
  minikube start \
    --profile modern-spring \
    --kubernetes-version=v1.24.4 \
    --container-runtime=docker \
    --cpus=4 \
    --memory=6GB \
    --disk-size=20g \
    --vm=true
}


function stop_cluster() {
  set -x
  minikube stop --profile modern-spring
}

function delete_cluster() {
  set -x
  minikube stop --profile modern-spring
  minikube delete --profile modern-spring
}

function restart_cluster() {
  stop_cluster
  set +x
  start_cluster
}

readonly ACTION=${1}

if [[ ${ACTION} == "start" ]]; then
  start_cluster
elif [[ ${ACTION} == "restart" ]]; then
  restart_cluster
elif [[ ${ACTION} == "stop" ]]; then
  stop_cluster
elif [[ ${ACTION} == "delete" ]]; then
   delete_cluster
else
  usage
  exit 1
fi