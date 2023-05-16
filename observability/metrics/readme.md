# metrics

This demo app shows how to define custom metrics in a spring boot application 
and export it to prometheus

## Things to try out 

1. Start the application 
2. go to [http://localhost:8080](http://localhost:8080) it will place simulated orders 
3. Go to [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
4. Go to [http://localhost:8080/actuator/metrics/orders.flagged](http://localhost:8080/actuator/metrics/orders.flagged) to get details on a custom metric 
5. Go to [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus) to view the same metrics in prometheus format
6. Run `docker compose up` to launch a prometheus server configured to pull metrics form the running boot application 
7. Go to [http://localhost:9090](http://localhost:9090]) to access the prometheus GUI 
8. Search for the `orders_placed_total` metric and display it 
9. look through the code in the project and notice the the places where micrometer metrics are used to
   export specific metrics. 
