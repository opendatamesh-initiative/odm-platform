package org.opendatamesh.platform.pp.params.server.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform's Params API Reference")
                        .summary("This page describe tha API exposed by ODM Platform's Params Service")
                        .description(
                                "This page describe tha API exposed by ODM Platform's Params Service" +
                                "\r\n# Overview" +
                                "\r\nThe Params Module of the Open Data Mesh platform manages the lifecycle of global or local configurations and parameters. Specifically, it handles the creation of a config/parameter, its update and allow other modules to retrieve the values"
                        )
                        .version("1.0.37")
                        .license(new License().name("Apache 2.0").url("https://github.com/opendatamesh-initiative/odm-platform/blob/main/LICENSE"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform's Params API Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }
}