package org.opendatamesh.platform.pp.blueprint.server.configs;

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
                        .title("ODM Platform's Blueprint API Reference")
                        .summary("This page describe tha API exposed by ODM Platform's Blueprint Service")
                        .description(
                                "This page describe tha API exposed by ODM Platform's Blueprint Service" +
                                "\r\n# Overview" +
                                "\r\nThe Blueprint Module of the Open Data Mesh platform manages the lifecycle of a blueprint. Specifically, it handles the creation of a blueprint, its update and the generation of a project from the blueprint given the right set of parameters"
                        )
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://github.com/opendatamesh-initiative/odm-platform/blob/main/LICENSE"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform's Blueprint API Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }
}