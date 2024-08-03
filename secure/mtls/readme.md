# mtls

Demo project showing how to configure mTLS on a spring boot server, and clients.
**The demo does NOT use spring security**, so on the server side you will need
to use the servlets api to get the client certificate to get the user details.
Example the samples under `secure/authenticate/x509` to learn how to do mTLS
integration with Spring Security.

