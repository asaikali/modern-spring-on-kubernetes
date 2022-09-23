# Discovery

This sample shows to use the Spring Cloud Kuberentes native service
discovery.

### Deploy to K8s

* build the container images locally using spring boot plugin `./mvnw -Dmaven.test.skip=true clean spring-boot:build-image`.
* deploy the app `kustomize build . | kubectl apply -f -`
* check contents of the discovery namespace `kc get all -n discovery` you will see the node ports of all the
  services for the greeter, billboard, and config server.
* use your browser to access the bill-board-client 