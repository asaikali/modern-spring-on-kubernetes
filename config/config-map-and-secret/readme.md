# Accessing Config Maps & Secrets from Spring Boot

* Build the container image using the command `./mvnw spring-boot:build-image`
* inspect the `k8s/config-example.yml` deployment manifest to see what will be deployed to k8s
* deploy the application to your local docker desktop k8s `kubectl apply -f k8s`
* check the contents of the `config-examples` space using the command `kubectl get all -n config-examples` you should
  see the port number of the service that the app is accessible on, the port number is picked randomly. below is 
  example output of what you should see
 ```text
 kc get all -n config-examples
NAME                                   READY   STATUS    RESTARTS   AGE
pod/message-service-6f694b6474-rrl75   1/1     Running   0          19s

NAME                      TYPE       CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
service/message-service   NodePort   10.109.134.6   <none>        8080:30165/TCP   19s

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/message-service   1/1     1            1           19s

NAME                                         DESIRED   CURRENT   READY   AGE
replicaset.apps/message-service-6f694b6474   1         1         1       19s
```

* using your browser make a request to the port number of the service, base on the output above 
  the request should be sent to `http://localhost:30165/` you should see a JSON response with the
  time the request was processed and a message that was set in the config map. Below is the output
  from my laptop.
```json
{
"date": "2021-05-27T01:08:55.379348",
"message": "Value from K8s config"
}
```
* modify the src/config-example.yaml to change the value of the message in the config map. For example I changed it 
 to
```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: message-service
  namespace: config-examples
data:
  message: "Value from K8s config updated"
```
* execute `kubectl apply -f k8s` to change the value of the config map
* Hit actuator refresh endpoint: `curl localhost:49670/actuator/refresh -d {} -H "Content-Type: application/json"`
* reload the page `http://localhost:30165/` and you see that the message element of the json response matches the 
  value from the config map. Spring Cloud for k8s was able to detect the change and reload the value when it was 
  changed because the `com.example.ConfigDetailsController` was marked with `@RefreshScope` which cause spring cloud
  to reload the bean when an injected value changes.
```json
{
"date": "2021-05-27T01:16:25.849256",
"message": "Value from K8s config updated"
}
```
* inspect the code in `ConfigDetailsController` class 
* inspect the pom.xml and notice the `spring-cloud-starter-kubernetes-client-config` dependency 
```xml
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-kubernetes-client-config</artifactId>
</dependency>
```
* inspect the `application.yml` and notice that the spring cloud k8s is being configured to watch for changes 
  on the tree config map called message-service 
  

**Configuration Tree**


* inspect `TreeController` class notice that it reads two property values
* inspect `k8s/config-example.yml` file notice the config map and how it is mounted into the container
* launch octant with `octant` command then visit `localhost:7777" with your browser
* In octant find the pod and pull up a terminal then navigate into `/myconfigs` and see the contents of the file (`kubectl exec --stdin --tty message-service-6fbb95bc88-g2m26 -- /bin/bash`)
* inspect the `application.yml` file notice the `import: "optional:configtree:/myconfigs/` which reads the contents of
  the `/myconfigs` as individual properties
* visit the http endpoint `/tree` on the exposed node port for example `curl http://localhost:32765/tree/` you will
  see the property values from the kubernetes config map that were converted in spring boot properties as shown below
 ```json
{
"bar": "bar value",
"foo": "foo value"
}
```
* change the bar and foo values in the config map and apply the changes to k8s, you will notice that there end point 
 does not update because the `TreeController` bean does not have the `@RefreshScope` on it.
  
