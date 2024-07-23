package com.in28minutes.microservices.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/retry-sample-api")
    @Retry(name = "api", fallbackMethod = "defaultFallbackResponse")
    public String retrySampleApi() {
        logger.info("In retrySampleApi method call");
        ResponseEntity<String> entity = new RestTemplate().getForEntity("http://localhost:8080/sammy", String.class);
        return entity.getBody();
    }

    @GetMapping("/circuit-breaker-sample-api")
    @CircuitBreaker(name = "api", fallbackMethod = "defaultFallbackResponse")
    public String circuitBreakerSampleApi() {
        logger.info("In circuitBreakerSampleApi method call");
        ResponseEntity<String> entity = new RestTemplate().getForEntity("http://localhost:8080/sammy", String.class);
        return entity.getBody();
    }

    @GetMapping("/rate-limit-sample-api")
    @RateLimiter(name = "default", fallbackMethod = "defaultFallbackResponse")
    public String rateLimitSampleApi() {
        logger.info("In rateLimitSampleApi method call");
        return "rate-limit-sample-api";
    }

    @GetMapping("/concurrent-sample-api")
    @Bulkhead(name = "default", fallbackMethod = "defaultFallbackResponse")
    public String concurrentSampleApi() {
        logger.info("In concurrentSampleApi method call");
        return "concurrent-sample-api";
    }

    public String defaultFallbackResponse(Exception ex) {
        return "default-response";
    }
}
