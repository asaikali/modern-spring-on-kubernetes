#!/bin/bash

set -eo pipefail

#
# Define usage and error functions
#
readonly BOLD=$(tput bold)
readonly NORMAL=$(tput sgr0)
readonly USAGE="

Deploys all the GCP infrastructure for a multicluster TAP depolyment

Usage: $(basename $0) [command]

Commands:
  create   - creates all the required GCP infrastructure
  delete   - delete all the gcp infrastructure creaed by create
  creds    - retrives kubecnofig files and service account access key

${BOLD}You need to configure a settings.sh${NORMAL}

You must have a ${BOLD}settings.sh${NORMAL} defines with enviroment
specific details such as dns name, and credentials to access Tanzunet
and github.com. There is a settings-template.sh with comments that
documents all the settings you need and why.

Examples:
  $(basename $0) create
  $(basename $0) delete
  $(basename $0) creds
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