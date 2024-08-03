
# command to make an rsa based private key (Works in Java 21)
# Generated cert has Signature Algorithm: sha256WithRSAEncryption
#openssl genpkey \
#  -algorithm rsa \
#  -out cert/demo.key

# command to make an rsa based private key (Does not work in Java 21)
# jdk claims there is no computable signature verification algorithm in ssl debug log
# Generate cert will have Signature Algorithm: ED25519
# seem that support is not wide spread / allowed by baseline requirements for browsers and ca's
# https://news.ycombinator.com/item?id=24537042
# https://security.stackexchange.com/questions/269725/what-is-the-current-april-2023-browser-support-for-ed25519-certificate-signatu
#openssl genpkey \
#  -algorithm Ed25519 \
#  -out cert/demo.key

# generate an elliptic curve private key (Works in Java 21)
# cert will have a   Signature Algorithm: ecdsa-with-SHA256
openssl ecparam \
  -genkey \
  -name prime256v1 \
  -out cert/server.key \

# Generate a self-signed X.509 certificate using the encrypted private key
openssl req -x509 \
  -subj "/CN=localhost" \
  -key cert/server.key \
  -out cert/server.crt \
  -days 365 \
  -nodes

