package com.in28minutes.microservices.currencyconversionservice;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrency(@PathVariable String from,
                                              @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> responseEntity = restTemplate.getForEntity("http://host.docker.internal:8000/currency-exchange/from/USD/to/INR",
                CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversion = responseEntity.getBody();
        return new CurrencyConversion(currencyConversion.getId(),
                currencyConversion.getFrom(), currencyConversion.getTo(),
                currencyConversion.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment()+" rest template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrencyFeign(@PathVariable String from,
                                              @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {
        CurrencyConversion currencyConversion = proxy.retrieveCurrencyExchange(from, to);
        return new CurrencyConversion(currencyConversion.getId(),
                currencyConversion.getFrom(), currencyConversion.getTo(),
                currencyConversion.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment()+" feign client");
    }
}
