package com.redhat.consulting.transaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.webjars.WebJarAssetLocator;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${openapi.transactionService.base-path:}")
    private String transactionServiceBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String swaggerUiVersion = new WebJarAssetLocator().getWebJars().get("swagger-ui");

        registry.addResourceHandler(transactionServiceBasePath + "/swagger-ui/**")
                .addResourceLocations("classpath:META-INF/resources/webjars/swagger-ui/" + swaggerUiVersion + "/");
    }

}
