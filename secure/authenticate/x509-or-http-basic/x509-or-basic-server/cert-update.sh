# Generate a self-signed X.509 certificate using the encrypted private key
openssl req -x509 \
  -subj "/CN=localhost" \
  -key cert/server.key \
  -out cert/server.crt \
  -days 2 \
  -passin pass:changeit
