#!/bin/bash

# Define the bold text format
bold=$(tput bold)
normal=$(tput sgr0)

# Install or upgrade CertManager
echo "${bold}Installing or upgrading CertManager...${normal}"
helm upgrade --install cert-manager cert-manager \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --create-namespace \
  --version v1.15.1 \
  --set crds.enabled=true \
  --wait

# Check if CertManager was successfully deployed
if [ $? -eq 0 ]; then
  echo "${bold}CertManager installed successfully.${normal}"
else
  echo "${bold}Failed to install CertManager.${normal}"
  exit 1
fi

# Install or upgrade Contour
echo "${bold}Installing or upgrading Contour...${normal}"
helm upgrade --install contour contour \
  --repo https://charts.bitnami.com/bitnami \
  --namespace contour \
  --create-namespace \
  --version 18.2.9 \
  --set envoy.service.type=NodePort \
  --set envoy.service.nodePorts.http=30080 \
  --set envoy.service.nodePorts.https=30443 \
  --set contour.resources.requests.cpu=100m \
  --set contour.resources.requests.memory=100Mi \
  --set contour.resources.limits.cpu=500m \
  --set contour.resources.limits.memory=500Mi \
  --set envoy.resources.requests.cpu=100m \
  --set envoy.resources.requests.memory=128Mi \
  --set envoy.resources.limits.cpu=1000m \
  --set envoy.resources.limits.memory=1024Mi \
  --set envoy.shutdownManager.resources.requests.cpu=50m \
  --set envoy.shutdownManager.resources.requests.memory=50Mi \
  --set envoy.shutdownManager.resources.limits.cpu=250m \
  --set envoy.shutdownManager.resources.limits.memory=256Mi \
  --wait

# Check if Contour was successfully deployed
if [ $? -eq 0 ]; then
  echo "${bold}Contour installed successfully.${normal}"
else
  echo "${bold}Failed to install Contour.${normal}"
  exit 1
fi

echo "${bold}Installation of CertManager and Contour completed.${normal}"
