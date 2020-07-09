# CNI Transaction Service

Example contract-first REST API for managing imaginary financial transactions.

## Contract First / API First

The API specifications were designed using [Apicurio Studio](https://www.apicur.io/studio/).

The specifications can be found at:

 - [classpath:specs/transaction-service.yaml](src/main/resources/specs/transaction-service.yaml)

### Code Generation

From the specifications, this project uses the [openapi-generator](https://openapi-generator.tech/)
maven plugin to regenerate the base Spring Boot-based REST service code on each compile.

Note that because the code is generated during compile, the implementation must not be overwritten
during generation. Use of the configuration option `delegatePattern` facilitates externalizing
the actual service implementation. Specifically, the entrypoint into implementation is contained
within [TransactionApiDelegateImpl.java](src/main/java/com/redhat/consulting/transaction/api/TransactionsApiDelegateImpl.java)

## Running the service

On Linux / Mac, run:

```shell script
./mvnw spring-boot:run
```

On Windows, run:

```shell script
mvnw.cmd spring-boot:run
```

Notice that by default, the `spring-boot:run` command runs with the `dev` profile activated. Differences
from the default to the `dev` profile can be see in
[classpath:config/application-dev.yaml](src/main/resources/config/application-dev.yaml).

## Testing the Service

While the service is running in `dev` mode, point your browser to:

    http://localhost:8080/dev/swagger-ui.html

## Remaining Tasks

Some ideas for further extension of this demo:

 - Build Helm charts for deploying into OpenShift
 - Use a different database when deployed in OpenShift (MySQL/PostgreSQL)
 - Unit Tests and Integration Tests
 - Add OpenAPI specification for separate balance service
 - Move transaction processing out, possibly pushing message to AMQ
   - Transaction processing could also be triggered by CDC (change data capture)
