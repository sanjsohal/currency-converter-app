package com.in28minutes.microservices.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p.path("/get")
                        .filters(f->f.addRequestHeader("MyRequestHeader","Mera Header")
                                .addRequestParameter("MyParam","param1"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/currency-exchange/**").filters(f -> f.filter(authenticationFilter)).uri("lb://currency-exchange"))
                .route(p -> p.path("/currency-conversion/**").filters(f -> f.filter(authenticationFilter)).uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-feign/**").filters(f -> f.filter(authenticationFilter)).uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                                "/currency-conversion-feign/${segment}").filter(authenticationFilter)).uri("lb://currency-conversion"))
                .build();
    }
}
