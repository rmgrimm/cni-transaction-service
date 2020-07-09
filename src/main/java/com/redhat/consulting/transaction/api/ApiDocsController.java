package com.redhat.consulting.transaction.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("${openapi.transactionService.base-path:}")
public class ApiDocsController {

    private final YAMLMapper yamlMapper;
    private final String transactionServiceOpenApiContent;

    @Autowired
    public ApiDocsController(@Qualifier("transactionServiceOpenApiContent")
                                     String transactionServiceOpenApiContent) {
        // Use direct instantiation here since YAMLMapper extends ObjectMapper and shouldn't pollute spring context with
        // an ObjectMapper that is actually handling YAML
        this.yamlMapper = new YAMLMapper();

        this.transactionServiceOpenApiContent = transactionServiceOpenApiContent;
    }

    @GetMapping(path = "/transaction-service.yaml",
            produces = "application/vnd.oai.openapi")
    @ResponseBody
    public String transactionServiceYaml() {
        return transactionServiceOpenApiContent;
    }

    @GetMapping(path = "/transaction-service.json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object transactionServiceJson() throws JsonProcessingException {
        return yamlMapper.readValue(transactionServiceOpenApiContent, Object.class);
    }

    @GetMapping(path = {"/", "/swagger-ui.html"})
    public String index() {
        return "redirect:swagger-ui/index.html";
    }

}
