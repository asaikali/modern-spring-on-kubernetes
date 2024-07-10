#!/bin/bash

# Define the output directory and certificate file path
CERT_FILE="selfsigned-ca.crt"


# Step 1: Create a self-signed ClusterIssuer
cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: selfsigned-cluster-issuer
spec:
  selfSigned: {}
EOF

# Wait for the ClusterIssuer to be ready
echo "Waiting for ClusterIssuer to be ready..."
sleep 10

# Step 2: Create a Certificate to generate the CA secret
cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: selfsigned-ca
  namespace: default
spec:
  isCA: true
  commonName: "selfsigned-ca"
  secretName: selfsigned-ca-secret
  issuerRef:
    name: selfsigned-cluster-issuer
    kind: ClusterIssuer
EOF

# Wait for the Certificate to be issued
echo "Waiting for Certificate to be issued..."
sleep 20

# Step 3: Retrieve the CA certificate from the secret
kubectl get secret selfsigned-ca-secret -n default -o jsonpath='{.data.ca\.crt}' | base64 --decode > "$CERT_FILE"

## Step 4: Add the CA certificate to macOS trust store
#sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain "$CERT_FILE"
#
#echo "CA certificate has been added to macOS trust store and saved to $CERT_FILE."
#
## Verify if the certificate is added
#security find-certificate -a -c "selfsigned-ca" /Library/Keychains/System.keychain
