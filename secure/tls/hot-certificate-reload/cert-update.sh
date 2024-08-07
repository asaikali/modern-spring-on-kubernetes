# Generate a self-signed X.509 certificate using the encrypted private key
openssl req -x509 \
  -subj "/CN=demo" \
  -key cert/demo.key \
  -out cert/demo.crt \
  -days 2 \
  -passin pass:changeit
