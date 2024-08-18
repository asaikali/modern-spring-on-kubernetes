#!/bin/bash

# Set the namespace
NAMESPACE="a-record"

# Get the NodePort assigned by Kubernetes in the specified namespace
NODE_PORT=$(kubectl get svc billboard-client -n $NAMESPACE -o=jsonpath='{.spec.ports[0].nodePort}')

# Construct the URL
URL="http://localhost:$NODE_PORT"

# Print the URL
echo "The URL of the NodePort service is: $URL"
