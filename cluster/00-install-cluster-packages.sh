#!/bin/bash

# Define the bold text format
bold=$(tput bold)
normal=$(tput sgr0)

# Define version numbers
CERT_MANAGER_VERSION="v1.15.1"
TRUST_MANAGER_VERSION="0.11.0"
CERT_MANAGER_CSI_DRIVER_VERSION="v0.9.0"
CERT_MANAGER_SPIFEE_CSI_DRIVER_VERSION="0.7.0"
CONTOUR_VERSION="18.2.9"
CLOUD_NATIVE_PG_VERSION="0.21.5"

# Install or upgrade CertManager
echo ""
echo "${bold}Installing or upgrading CertManager...${normal}"
echo ""
helm upgrade --install cert-manager cert-manager \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --create-namespace \
  --version ${CERT_MANAGER_VERSION} \
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

# Install or upgrade TrustManager
echo ""
echo "${bold}Installing or upgrading TrustManager...${normal}"
echo ""
helm upgrade --install trust-manager trust-manager \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --version ${TRUST_MANAGER_VERSION} \
  --wait

# Check if TrustManager was successfully deployed
if [ $? -eq 0 ]; then
  echo ""
  echo "${bold}TrustManager installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install TrustManager.${normal}"
  echo ""
  exit 1
fi

# Install or upgrade CertManager CSI driver
echo ""
echo "${bold}Installing or upgrading CertManager CSI driver...${normal}"
echo ""
helm upgrade --install cert-manager-csi cert-manager-csi-driver \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --version ${CERT_MANAGER_CSI_DRIVER_VERSION} \
  --wait

# Check if CertManager CSI driver was successfully deployed
if [ $? -eq 0 ]; then
  echo ""
  echo "${bold}CertManager CSI driver installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install CertManager CSI driver.${normal}"
  echo ""
  exit 1
fi

# Install or upgrade SPIFEE CertManager CSI driver
echo ""
echo "${bold}Installing or upgrading SPIFEE CertManager CSI driver...${normal}"
echo ""
helm upgrade --install cert-manager-csi-driver-spiffe cert-manager-csi-driver-spiffe \
  --repo https://charts.jetstack.io \
  --namespace cert-manager \
  --version ${CERT_MANAGER_SPIFEE_CSI_DRIVER_VERSION} \
  --wait

# Check if SPIFEE CertManager CSI driver was successfully deployed
if [ $? -eq 0 ]; then
  echo ""
  echo "${bold}SPIFEE CertManager CSI driver installed successfully.${normal}"
  echo ""
else
  echo ""
  echo "${bold}Failed to install SPIFEE CertManager CSI driver.${normal}"
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
  --version ${CONTOUR_VERSION} \
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
  --version ${CLOUD_NATIVE_PG_VERSION} \
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
echo "${bold}Installation of CertManager, TrustManager, CertManager CSI driver, SPIFEE CertManager CSI driver, Contour, and CloudNativePG completed.${normal}"
echo ""

# List all installed Helm packages
echo ""
echo "${bold}Listing all installed Helm packages...${normal}"
echo ""
helm list --all-namespaces
