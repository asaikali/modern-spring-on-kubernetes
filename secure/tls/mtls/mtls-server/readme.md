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







This project shows you how to configure mTLS on a spring boot application 

## Run the application 

1. Generate a certificate by running `./cert-create.sh`
2. Inspect the generated certificate by running `./cert-inspect.sh` 
3. Notice that the certificate expires in 1 day 
4. run the application. If you run into any errors it probably because of issues
   with the path name in the application.yaml because of current working 
   directory you might need to change it. It is configured out work out of the 
   box with intellij. 
5. run `curl -k -v https://localhost:8443` and you should get a response
6. notice that the debug output shows that the cert expires in 1 day 
6. run the `cert-update.sh` script
7. run the `cert-inspect.sh` script notice that the cert expires in 2 days 
8. look at the console log of the boot application you will see a log line 
   indicating that the app is running. For example
```text
2024-07-04T00:57:44.722+02:00  INFO 5429 --- [-bundle-watcher] o.a.t.util.net.NioEndpoint.certificate   : Connector [https-jsse-nio-8443], TLS virtual host [_default_], certificate type [UNDEFINED] configured from keystore [/Users/adib/.keystore] using alias [tomcat] with trust store [null]
```
9. run `curl -k -v https://localhost:8443` and you should get a response.
10. Notice the cert expiry date in the response is now 2 days from now. 
    showing that the boot application picked up the new certificate without needing a restart. 
 
