# custom-resource

Example showing to define a Kubernetes Custom Resource and use it to 
configure a Spring Boot application. The application watches the api 
server for changes that affects ints configuration and reacts in real time
to those changes.

## Prerequisites 

You will need a Kubernetes cluster to test this application. A local cluster 
such as docker desktop or minikube is enough for testing. 

## Run the application 

### Understand the Greeting Custom Resource 
1. read through the yaml in `k8s/greetings.yaml` file it shows the 
   Kubernetes custom resource that we want to watch directly from the application.
2. read through the custom resource definition yaml in `k8s/crd.yaml` this 
   defines the schema for the Greeting custom resource

### Install the crd and resources 

1. `kubctl apply -f k8s/crd.yaml` 
2. `kubect get crds` and check that the output contains `greetings.example.com`
3. `kubectl apply -f k8s/greetings.yaml`
4. Validate the objects are created by running `kubectl get Greetings -n greeter`
   you should see output like this.
```text
   NAME                  AGE
   hello-world-english   11s
   hello-world-french    11s
```

### run the application from your IDE

1. Run the application from your IDE `src/main/java/com/example/KubernetesCustomResourceApplication.java` 
2. Check the output on the console you should see something like output below as part the log messages
```text
Greeting added: Hello, World!
Greeting added: Bonjour, le monde!
```
3. Issue an http get to the root of the application `http :8080/` you should see a json object 
   similar to the one below
```text
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/json
Date: Sat, 07 Sep 2024 07:17:29 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "date": "2024-09-07T03:17:29.049959",
    "message": "Hello, World!"
}

```

4. Issue a request for a French language greeting with the command `http :8080/ lang==fr` you should
see a response like the one below. 

```json
{
    "date": "2024-09-07T03:18:58.359392",
    "message": "Bonjour, le monde!"
}
```

5. notice that the application is reading the message from the Greeting object we applied earlier. 
  lets prove it by editing the `k8s/greetings.yaml` for example change the French message by adding 
   a 2 to the end of the message field in the spec. run  `kubectl apply -f k8s/greetings.yaml` 
6. Run the command `http :8080/ lang==fr` you should see the updated message  
```json
{
"date": "2024-09-07T03:22:37.612216",
"message": "Bonjour, le monde! 2"
}
```

### Understand the code 

1. Study the code in `src/main/java/com/example/kubernetes/client` it is utility code for 
   initializing the official Kubernetes java client library. 
2. Put a breakpoint in `com.example.RootController` and step through the code, notice the code in the 
   greeter package
3. Study the code in `src/main/java/com/example/kubernetes/ClientController.java` to learn the basics 
   of the java client for k8s, try the controller methods  
4. Study the code in `src/main/java/com/example/greeter/GreetingInformer.java` is the main place
   where the watch is put on the Greeting resource.

### Run the code in Kuberentes 

1. build a container image using `./mvnw spring-boot:build-image`
2. `kubectl apply -f server.yaml` 
3. using a tool like k9s check that the app is running and you should see that 
it is picking up the messages in the cluster by looking at the console output.
4. read through the yaml in `k8s/server.yaml` it is a fairly standard k8s 
   yaml that defines RBAC permissions, a deployment and a service.
5. try Invoking the greeter through the node port service 

## Resources 
[Kubernetes JavaClient](https://github.com/kubernetes-client/java)



