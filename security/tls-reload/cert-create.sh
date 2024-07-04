
# command to make an rsa based private key (Works in Java 21)
# Generated cert has Signature Algorithm: sha256WithRSAEncryption
#openssl genpkey \
#  -algorithm rsa \
#  -out cert/demo.key

# command to make an rsa based private key (Does not work in Java 21)
# it claims there is no computable signature verification algorithm
# Generate cert will have Signature Algorithm: ED25519
#openssl genpkey \
#  -algorithm ed25519 \
#  -out cert/demo.key

# generate an elliptic curve private key (Works in Java 21)
# cert will have a   Signature Algorithm: ecdsa-with-SHA256
openssl ecparam \
  -genkey \
  -name prime256v1 \
  -out cert/demo.key \

# Generate a self-signed X.509 certificate using the encrypted private key
openssl req -x509 \
  -subj "/CN=demo" \
  -key cert/demo.key \
  -out cert/demo.crt \
  -days 1 \
  -nodes

