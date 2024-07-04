# k8s-boot  

Example showing how to  configure Kubernetes Health & Readiness Probes using spring boot 
actuators.

**build and run the app** 

* build the app `./mvnw clean package` to produce the fat jar 
* build the container `docker build . -t k8s-boot:1` 
* run the container `docker run -p 8080:8080 -t k8s-boot:1`
* test the app using a browser `http://localhost:8080/`

**Actuator Health Check**

* using a web browser or curl go to `http://127.0.0.1:8080/actuator/health` 
* notice the `livenessState` and `readinessState` which are designed for use Kubernetes     
* inspect the `src/main/java/resources/application.yml` notice the probes enabled flag. This
  flag is set automatically when the app is launched in k8s. We are turning it on so you can 
  explore the app outside of k8s to understand the spring boot behavior.

**Readiness Probe**

A failing readiness probe causes k8s to stop sending requests to the app container. readiness 
definition from the Spring Boot [docs](https://docs.spring.io/spring-boot/docs/2.4.0-RC1/reference/htmlsingle/#boot-features-application-availability) 

> The “Readiness” state of an application tells whether the application is ready to handle traffic. 
A failing “Readiness” state tells the platform that it should not route traffic to the application 
>for now. This typically happens during startup, while CommandLineRunner and ApplicationRunner 
>components are being processed, or at any time if the application decides that it’s too busy for 
>additional traffic.

> An application is considered ready as soon as application and command-line runners have been 
>called, see Spring Boot application lifecycle and related Application Events.

* using a web browser or curl go to `http://127.0.0.1:8080/actuator/health/readiness` it should 
  report up, this is dedicated readiness probe.
  
* using a browser go to `http://127.0.0.1:8080/readiness/fail` it will cause the readiness probe 
  to fail. 
  
* using a browser go to `http://127.0.0.1:8080/actuator/health` you should see the overall app 
  status is OUT_OF_SERVICE 
  
* using a browser go to `http://127.0.0.1:8080/` notice the app is still working. The output of 
  the readiness state is for kuberenets to stop sending traffic to the app, it does cause the app to 
  stop accepting network connection.  
  
* using a browser go to `http://127.0.0.1:8080/readiness/pass` it will cause the readiness probe 
    to start passing gain.
    
* using a browser go to `http://127.0.0.1:8080/` you will notice overall app status is back up that
  signals to kuberenets to start sending the app requests. 

*  Inspect the `ProbesController` class to see how the application publishes events that cause 
  it to transition it's state. 

**Liveness Probe**

A failing liveness probe causes k8s to restart the container. Liveness definition From the Spring 
Boot [docs](https://docs.spring.io/spring-boot/docs/2.4.0-RC1/reference/htmlsingle/#boot-features-application-availability)

> The internal state of Spring Boot applications is mostly represented by the Spring 
ApplicationContext. If the application context has started successfully, 
Spring Boot assumes that the application is in a valid state. An application is considered 
live as soon as the context has been refreshed

* using a web browser or curl go to `http://127.0.0.1:8080/actuator/health/liveness` it should 
  report up, this is dedicated liveness probe.
  
* using a browser go to `http://127.0.0.1:8080/liveness/fail` it will cause the liveness probe 
  to fail. 
  
* using a browser go to `http://127.0.0.1:8080/actuator/health` you should see the overall app 
  status is DOWN  
  
* using a browser go to `http://127.0.0.1:8080/` notice the app is still working. The output of 
  the liveness state is for kubernetes to stop and restart the app container. 
   
* using a browser go to `http://127.0.0.1:8080/liveness/pass` it will cause the liveness probe 
    to start passing gain.
    
* using a browser go to `http://127.0.0.1:8080/` you will notice overall app status is back up.

* Inspect the `ProbesController` class to see how the application publishes events that cause 
  it to transition it's state. 
  
**deploy to k8s**

now that you understand livensess probes and readiness probes lets see them in action on k8s.  

*The following steps assume you have a K8s cluster running on your laptop. Docker Desktop Kubernetes
or minikube are sufficient. If you are using a remote cluster you will need to adapt the K8s 
deployment manifests to expose the deployment to your laptop.*

* terminate the container using `Ctrl+C` or `docker kill`
* execute `kubectl get all` to validate that you have access to a Kubernetes cluster.
* K8s deployment manifests are in the `k8s` directory. Open a terminal then execute `cd k8s`.
* inspect `src/deployment.yml` file and notice the readiness probes and liveness probes are pointing
  to the Spring Boot Actuators dedicated k8s probes you accessed in the previous steps. 
* execute `kubectl apply -f deployment.yml` 
* inspect `src/service.yml` notice that it exposes the container as a NodePort. 
* execute `kubectl apply -f service.yml` 
* execute `kubectl get all` it will show NodePort mapping. NodePort value is picked randomly from 
an available port by k8s, therefore your NodePort will be different. On my machine it picked `31264`
as the port number, your machine will probably have a different value. note the node port value and 
use it in the following steps to reach the application url.
* using your browser access the NodePort of the app for example `http://localhost:31264/` you 
  should some randomly rotating quotes. 
* Access the application probes
* using a web browser or curl go to spring boot probes on the app Node Port for example. 
  * `http://127.0.0.1:31264/actuator/health` notice the `livenessState` and `readinessState`
     which are designed for use Kubernetes
  * `http://127.0.0.1:31264/actuator/health/liveness` it should report up, this is dedicated 
     liveness probe for spring boot and k8s.
  * `http://127.0.0.1:31264/actuator/health/readiness` it should report up, this is dedicated 
     readiness probe.

**Fail Readiness Probe on K8s**

* Open a browser tab and go to running k8s container via node port `http://localhost:31264/` you 
  should see the rotating quotes. leave that window open and in your view.
* Open the browser inspector and go to the network tab, notice the recuring http requests going out
  ever few seconds. 
* open a second  browser tab and go `http://localhost:31264/readiness/fail` which will cause the 
  readiness probe to fail. 
* go to `http://localhost:31264/actuator/health/readiness` and you should see that the app is
  marked out of service.
* Wait 30+ seconds and you will see that rotating quotes will start producing an error because 
  k8s is no longer sending requsets to the container. There is  no way to send a `/pass` request
  because we can no longer reach the container via the NodePort. 
* use kubctl to delete the pod for the container with the failing readiness probe. One way to do this
  * `kubectl get pods`
  * note the pod name 
  * `kubectl delete pod-name-goes-here` will delete the pod, k8s will notice and  recerate it. 
* You should see the quotes are showing up again and are rotating. 

**Fail Liveness Probe on K8s**

* in a command prompt run `kubectl get  pods -w` and keep an eye on the application pod 
* go to `http://localhost:31264/liveness/fail` that will cause the liveness probe to fail 
* in a few seconds you will notice that the ready state of the pod you are watching transitions 
  from `1/1` to `0/` ready when the container is restarting, then back to `1/1` as show in the 
  sample output below.
  
```
NAME                         READY   STATUS    RESTARTS   AGE
k8s-boot-87bd5c599-9fgrc   1/1     Running   0          10m
k8s-boot-87bd5c599-9fgrc   0/1     Running   1          10m
k8s-boot-87bd5c599-9fgrc   1/1     Running   1          10m
```

* once the container restarts the app is healthy again. 

**Graceful shutdown**

* inspect the `SlowController` class in your editor, notice it goes to sleep for 10 seconds then
  returns a response. 
* run the app from the command prompt `./mvnw spring-boot:run`
* visit the app on `http://localhost:8080/slow` wait 10 for the response
*  visit the app on `http://localhost:8080/slow`, then head to the console where you can the app 
   and hit `ctrl+c` to interrupt the app while it is running  
* the app will exist, and you will see an exception printed on the console. 
* check the browser where you issued the `/slow` request, you will see a browser connection error
* edit the `src/main/resources/application.yml` and uncomment the graceful shutdown line, now 
  spring boot will wait for executing requests to complete before shutting down. 
* run the app from the command prompt `./mvnw spring-boot:run`
* visit the app on `http://localhost:8080/slow`, then head to the console where you can the app 
  and hit `ctrl+c` to interrupt the app while it is running  
* You will output similar to the one below indicating that spring  boot is waiting for the 
  current active request to finish, and you will see a response come back in the browser. 
  
```
2020-11-05 23:56:08.699  INFO 38965 --- [extShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2020-11-05 23:56:08.713  INFO 38965 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
```

**Pre Stop hook**

From the [Spring Boot docs](https://docs.spring.io/spring-boot/docs/2.4.0-RC1/reference/htmlsingle/#cloud-deployment-kubernetes)
>When Kubernetes deletes an application instance, the shutdown process involves several 
>subsystems concurrently: shutdown hooks, unregistering the service, removing the instance 
>from the load-balancer…​ Because this shutdown processing happens in parallel (and due 
>to the nature of distributed systems), there is a window during which traffic can be routed 
>to a pod that has also begun its shutdown processing.

* [K8s shutdown hook](https://kubernetes.io/docs/concepts/containers/container-lifecycle-hooks/)
* [Open Spring Boot Issue  remove need for pre stop hook](https://github.com/spring-projects/spring-boot/issues/20995)

**Programmatically detect running in K8s** 

* inspect the `QuoteController` class and notice the logic that detects if the app is running 
  on k8s

**Configuration Tree**

* inspect `ConfigController` class notice that it reads two property values
* inspect `k8s/deployment.yml` file notice the config map and how it is mounted into the container 
* launch octant with `octant` command then visit `localhost:7777" with your browser
* In octant find the pod and pull up a terminal then navigate into `/myconfigs` and see the contents of the file
* inspect the `application.yml` file notice the `import: "optional:configtree:/myconfigs/` which reads the contents of 
  the `/myconfigs` as individual properties
* visit the http endpoint `/config` on the exposed node port for example `curl http://localhost:32765/config/` you will
  see the property values from the kubernetes config map that were converted in spring boot properties as shown below
 ```
{"message":"hello there","test":"an example test property"}
```

## Resources
* Relevant sections from Spring Boot docs  
  * [Application Availability](https://docs.spring.io/spring-boot/reference/features/spring-application.html#features.spring-application.application-availability)
  * [Kubernetes Probes](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.kubernetes-probes)
  * [Boot Kubernetes Deployment Guide](https://docs.spring.io/spring-boot/how-to/deployment/cloud.html#howto.deployment.cloud.kubernetes)
  * [Graceful shutdown](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html#page-title)
  * [Configuration Tree](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.files.configtree)

* Guides and blog posts 
  * [Liveness and Readiness Probes with Spring Boot](https://spring.io/blog/2020/03/25/liveness-and-readiness-probes-with-spring-boot) 
  * [Spring on Kubernetes](https://spring.io/guides/topicals/spring-on-kubernetes/)
  * [Config file processing in Spring Boot 2.4](https://spring.io/blog/2020/08/14/config-file-processing-in-spring-boot-2-4)
  * [Delaying Shutdown to Wait for Pod Deletion Propagation](https://blog.gruntwork.io/delaying-shutdown-to-wait-for-pod-deletion-propagation-445f779a8304)
