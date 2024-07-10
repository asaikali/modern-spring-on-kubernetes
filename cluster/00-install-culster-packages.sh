#!/bin/bash

# Define the bold text format
bold=$(tput bold)
normal=$(tput sgr0)

# Install or upgrade CertManager
echo ""
echo "${bold}Installing or upgrading CertManager...${normal}"
echo ""
helm upgrade --install cert-manager cert-manager \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --create-namespace \
  --version v1.15.1 \
  --set crds.enabled=true \
  --wait

# Check if CertManager was successfully deployed
if [ $? -eq 0 ]; then
  echo ""
  echo "${bold}CertManager installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install CertManager.${normal}"
  echo ""
  exit 1
fi

# Install or upgrade Contour
echo ""
echo "${bold}Installing or upgrading Contour...${normal}"
echo ""
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
  echo ""
  echo "${bold}Contour installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install Contour.${normal}"
  echo ""
  exit 1
fi

# Install or upgrade CloudNativePG database operator
echo ""
echo "${bold}Installing or upgrading CloudNativePG database operator...${normal}"
echo ""
helm upgrade --install cnpg cloudnative-pg \
  --repo https://cloudnative-pg.github.io/charts \
  --namespace cnpg \
  --create-namespace \
  --version 0.21.5 \
  --wait

# Check if CloudNativePG was successfully deployed
if [ $? -eq 0 ]; then
  echo ""
  echo "${bold}CloudNativePG database operator installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install CloudNativePG database operator.${normal}"
  echo ""
  exit 1
fi

echo ""
echo "${bold}Installation of CertManager, Contour, and CloudNativePG completed.${normal}"
echo ""

# List all installed Helm packages
echo ""
echo "${bold}Listing all installed Helm packages...${normal}"
echo ""
helm list --all-namespaces
