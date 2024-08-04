# x509-client

This project shows you how to configure how to configure the various Spring
http clients to use X.509 certificates to perform mutual tls and extract 
the identity of the client on the server side using spring security x509 
authentication provider.

## Make sure that mtl-server is running before you run this sample

1. Follow the instructions in the `x509-server` project to run the server

## Generate Client Certificate

1. Generate a certificate by running `./cert-create.sh`
2. Inspect the generated certificate by running `./cert-inspect.sh`

## Run the application

1. run the application. If you run into any errors it probably because of issues
   with the path name in the application.yaml because of current working
   directory you might need to change it. It is configured out work out of the
   box with intellij.
2. Inspect the output of the application you will see 4 calls to the server made
   using RestClient, RestTemplate, WebClient, and DeclarativeClient

## manually request using curl

1. Run curl and pass it the client certs and self-signed cert of the server

```shell
curl https://localhost:8443  \
  --cert cert/client.crt \
  --key cert/client.key \
  --cacert ../x509-server/cert/server.crt \
  -vvv
```

2. Try curl with no client certs and you will get an error message

```shell
curl https://localhost:8443  \
  --cacert ../x509-server/cert/server.crt \
  -vvv
```

## Notes about the sample code

1. Client is configured using the Spring Boot SslBundles Feature
2. Self-signed certificates are used so the `application.yaml` of the server
   and the client need to trust each other by configuring a truststore in the
   bundles.
3. The Declarative interfaces can be used with any of the underlying http request
   abstractions in Spring including RestClient, RestTemplate, and WebClient.



