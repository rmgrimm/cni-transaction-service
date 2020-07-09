package com.redhat.consulting.transaction.config;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Configuration
public class MiscConfig {

    @Bean
    public String transactionServiceOpenApiContent(
            @Value("classpath:specs/transaction-service.yaml") Resource transactionResource,
            @Value("${openapi.transactionService.base-path:/}") String transactionServiceBasePath) throws IOException {
        String yamlContent;
        try (InputStream is = transactionResource.getInputStream()) {
            yamlContent = StreamUtils.copyToString(is, Charset.defaultCharset());
        }

        // Adjust the paths for the base path specified in application.yaml and variants
        return yamlContent.replaceAll("url: /", "url: " + transactionServiceBasePath);
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
