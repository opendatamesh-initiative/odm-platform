package org.opendatamesh.platform.up.metaservice.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationOpenApiConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springshop-public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform's Notification API Reference")
                        .summary("This page describe tha API exposed by ODM Platform's Notification Service")
                        .description(
                                "This page describe tha API exposed by ODM Platform's Notification Service" +

                                "\r\n# Overview" +
                                "\r\nThe Notification Module of the Open Data Mesh platform manages the lifecycle of notifications. Specifically, it handles the reception of a notification for a specific target system."
                        )
                        .version("0.9.0")
                        .license(new License().name("Apache 2.0").url("https://github.com/opendatamesh-initiative/odm-platform/blob/main/LICENSE"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform's Notification API Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }

}
