# Generate a self-signed X.509 certificate using the encrypted private key
openssl req -x509 \
  -subj "/CN=client" \
  -key cert/client.key \
  -out cert/client.crt \
  -days 2 \
  -passin pass:changeit
