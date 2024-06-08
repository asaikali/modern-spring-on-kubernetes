mkdir certs
cd certs
openssl req -x509 -subj "/CN=demo-cert-1" -keyout demo.key -out demo.crt -sha256 -days 365 -nodes -newkey ed25519
