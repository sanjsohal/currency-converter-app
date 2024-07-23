package com.in28minutes.microservices.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class CurrencyExchangeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CurrencyExchangeRepository repository;

    @Autowired
    private Environment environment;

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retrieveCurrencyExchange(@PathVariable String from, @PathVariable String to) {
        logger.info("retrieveCurrencyExchange, from: {}, to: {}", from, to);
        CurrencyExchange exchange = repository.findByFromAndTo(from, to);
        if(exchange == null) {
            throw new RuntimeException("Unable to find currency for from "+from+" and to "+to);
        }
        String port = environment.getProperty("local.server.port");
        exchange.setEnvironment(port);
        return exchange;
    }
}
