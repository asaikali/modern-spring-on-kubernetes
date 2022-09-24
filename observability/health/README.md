# Health Check Indicator

This application contains a custom 
[health indicator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-health) 
that can be toggled to learn the mechanics of how to build a custom health check indicator in 
SpringBoot. This app is also quite useful for experimenting with health check configurations 
in Kubernetes and Cloud Foundry deployment manifests. 

Run the application and check that it cf consider's it healthy. Then send get request to `/fail` 
to make the health indicator fail this will cause  `/actuator/health` to start failing.

There are two health groups defined make sure to visit `/actuator/health/foo` 
and `/actuator/health/bar`. Health groups make it possible to select subsets of the health 
indicators that you can use within Kubernetes readiness probes, or health probes.

```java
@Component
public class ExampleHealthIndicator implements HealthIndicator
{
    private boolean state = true;

    @Override
    public Health health() {
        System.out.println("ExampleHealthIndicator called at " + LocalDateTime.now() + " state=" + state);
        if(state) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
```

The app provides a set of end points to toggle the state of the health indicator as shown in the code below.

```java
@RestController
public class RootController {

    @Autowired
    private ExampleHealthIndicator exampleHealthIndicator;

    @GetMapping
    public String get()
    {
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }

    @GetMapping("/fail")
    public String fail()
    {
        exampleHealthIndicator.setState(false);
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }

    @GetMapping("/pass")
    public String pass()
    {
        exampleHealthIndicator.setState(true);
        return "Health Checks Will Pass: "  + exampleHealthIndicator.getState();
    }
}
```

# Cloud Foundry Configuration

Run the application and check that it cf consider's it healthy. Then send get request to `/fail` 
to make the health indicator fail this will cause  `/actuator/health` to start failing.

health check failures will be visible in the log stream from the application so make sure to monitor the
app logs using the `cf logs` command to see the reports of health check failures. 
 
Cloud Foundry can check application health using three methods process, port and http end point.
These methods are documented at <https://docs.cloudfoundry.org/devguide/deploy-apps/healthchecks.html>

The http end point health check can be used with spring boot applications to point at the actuator
health endpoint. CF will issue an HTTP GET on `/actuator/health` every 30 seconds if it gets and 
`HTTP 200` response then it will consider the app healthy, if not it will restart the app. 

When deploying the application you need to request the http health check via `cf` cli arguments or in 
the deployment manifest by adding the following.
```yaml
  health-check-type: http
  health-check-http-endpoint: /actuator/health
``` 
