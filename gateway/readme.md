# Discovery

### Deploy to K8s

* build the container images locally using spring boot plugin `./mvnw clean spring-boot:build-image`.
* deploy the app `kustomize build . | kubectl apply -f -`
* check contents of the gateway namespace `kc get all -n gateway` you will see the node ports of all the
  services for the greeter, billboard, gateway-server.
* use your browser to access the gateway server 

