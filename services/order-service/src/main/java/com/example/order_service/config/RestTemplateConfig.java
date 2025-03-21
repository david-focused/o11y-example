package com.example.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Value("${service.inventory.url}")
    private String inventoryServiceUrl;

    @Value("${service.shipping.url}")
    private String shippingServiceUrl;

    @Bean(name = "inventoryRestTemplate")
    public RestTemplate inventoryRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(inventoryServiceUrl))
                .build();

        return restTemplate;
    }

    @Bean(name = "shippingRestTemplate")
    public RestTemplate shippingRestTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(2000);
        factory.setConnectTimeout(2000);
        
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(shippingServiceUrl))
                .requestFactory(() -> factory)
                .build();

        return restTemplate;
    }
}
