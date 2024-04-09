package org.opendatamesh.platform.pp.event.notifier.server.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class EventNotifierOpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform Product Plane Event Notifier Service API Reference")
                        .summary("This page describe tha API exposed by the Event Notifier Service Server of the Product Plane " +
                                "of the Open Data Mesh Platform.")
                        .description(
                                "This page describe tha API exposed by the Event Notifier Service Server of the Product Plane of the Open Data Mesh Platform." +

                                "\r\n# Overview" +
                                "\r\nThe Event Notifier Module of the Open Data Mesh platform manages the lifecycle of Observers and the dispatch of Notification to them." +
                                "An Observer is an Adapter of the Notification service in the Utility Plane"
                        )
                        .version("0.9.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }

}
