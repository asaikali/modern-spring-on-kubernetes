# mtls-server

This project shows you how to configure how to configure a spring boot based 
server to require mtls. 

## Generate Server Certificate

1. Generate a certificate by running `./cert-create.sh`
2. Inspect the generated certificate by running `./cert-inspect.sh`

## Run the server

1. run the application. If you run into any errors it probably because of issues
   with the path name in the application.yaml because of current working
   directory you might need to change it. It is configured to work out of the
   box with intellij.

## Test the server

1. Follow the instructions in the `mtls-client` project

## Notes about the sample code

1. Server is configured using the Spring Boot SslBundles Feature
2. Self-signed certificates are used so the `application.yaml` of the server
   and the client need to trust each other by configuring a truststore in the
   bundles.
3. The mTLS authentication is done by the underlying web server, tomcat, jetty,
   netty ... etc. The application layer can read the client certificate from
   the servlet request path but there is no spring security configured, check
   the `secure/autheticate/x509` projects to learn how to configure x.509 based
   spring security authentication provider.
