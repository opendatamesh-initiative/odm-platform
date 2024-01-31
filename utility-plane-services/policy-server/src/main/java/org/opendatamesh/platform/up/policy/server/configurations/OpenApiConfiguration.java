package org.opendatamesh.platform.up.policy.server.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform's Policy API Reference")
                        .summary("This page describe tha API exposed by ODM Platform's Policy Service")
                        .description(
                                "This page describe tha API exposed by ODM Platform's Policy Service" +
                                "\r\n# Overview" +
                                "\r\nTODO"
                        )
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://github.com/opendatamesh-initiative/odm-platform/blob/main/LICENSE"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform's Policy API Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }

}
