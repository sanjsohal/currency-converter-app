package com.in28minutes.microservices.apigateway;


import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(validator.isSecured.test(request)) {
           if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
               return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
           }
           String authHeaders = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
           if(authHeaders == null || !authHeaders.startsWith("Bearer")) {
               return this.onError(exchange, "No Bearer token", HttpStatus.UNAUTHORIZED);
           }
           authHeaders = authHeaders.substring(7);
           try {
               jwtUtil.validateToken(authHeaders);
           } catch(Exception ex) {
               return this.onError(exchange, "Invalid token", HttpStatus.FORBIDDEN);
           }
        }
        return chain.filter(exchange); // Forward to route
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}