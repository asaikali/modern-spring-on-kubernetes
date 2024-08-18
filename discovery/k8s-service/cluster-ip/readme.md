# dns

Kubernetes assign each ClusterIP service a name in the DNS system of the cluster. 
This can be used to do service discovery simply by using the k8s assigned dns 
name. In this scenario Kubernetes does the LoadBalancing there is no service 
discovery at all.

### Deploy to K8s

* build the container images locally using spring boot plugin `./mvnw clean spring-boot:build-image`.
* deploy the app `kustomize build . | kubectl apply -f -`
* check contents of the discovery-dns namespace `kc get all -n discovery-dns` you will see the node ports of all the
  services for the greeter, billboard, and config server.   
* use your browser to access the bill-board-client 
