# tracing

This sample shows you how to use distributed tracing capabilities in micrometer
which has replaced spring cloud sleuth in Spring Boot 3.0 and Spring Framework 6.

## Things to try out 

1. Run `docker compose up` to launch a local zipkin server in docker container
2. Go to [http://127.0.0.1:9411/](http://127.0.0.1:9411/) to try see the zipkin UI 
3. run the `message-service` application  
4. run the `billboard-client` application 
5. Go to [http://localhost:8080](http://localhost:8080) this will start sending 
   requests to the billboard service which will call the message service.
6. You should see log lines on the `billboard-client` app that show the traceIds
   which you can use in the zipkin UI to find a trace
7. In the zipkin UI locate a distributed trace and explore the trace
8. Notice that the distributed trace shows the SQL queries that are being sent 
   by the message-service to the database using Spring Data JPA

## Explore the code 

1. Inspect the code in message-service  `TracingMessageServiceApplication.java`
   notice that we have to add an aspect so to the project so that the `@Observed`
   annotation work. 

2. Read through the code in the `MessageService` class and notice the `@Observerd`
   annotations which are used to define a new span to for the code that executes
   in the method. Also notice the use of baggage.

3. read through the code in the `BillboardController` and notice how it sets 
   baggage that it sends to the message service. This baggage can then be used
   to store in generated span tags.

