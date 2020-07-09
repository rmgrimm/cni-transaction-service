package com.redhat.consulting.transaction.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public final class ApiUriSupport {

    @Value("${openapi.transactionService.base-path:}")
    private String transactionBasePath;

    /**
     * Since the Spring facilities to generate a URI do not appropriately template Spring-style placeholders,
     * this method will replace the first path segment with any basePath as configured.
     *
     * @param origUriComponentsBuilder the result of calling {@link org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMethodCall(Object)}
     *                                 to the appropriate method call
     * @return a {@link URI} that is appropriate for referring to a method call
     */
    public URI buildTransactionLocation(UriComponentsBuilder origUriComponentsBuilder) {
        UriComponents uriComponents = origUriComponentsBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uriComponents.toUri())
                .replacePath(transactionBasePath);
        uriComponents.getPathSegments().stream()
                .skip(1)
                .forEachOrdered(uriComponentsBuilder::pathSegment);

        return uriComponentsBuilder.build().toUri();
    }
}
