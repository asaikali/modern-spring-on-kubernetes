// package com.example;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
// public class GatewayConfigurer {
//
//    @Autowired
//    private TokenRelayGatewayFilterFactory filterFactory;
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//
//        return builder.routes()
//                .route("api", r -> r.path("/api/**")
//                        .filters(f -> f.filter(filterFactory.apply()))
//                        .uri("lb://message-service"))
//                .route("angular", r -> r.path("/**")
//                        .uri(frontend))
//                .build();
//    }
// }
