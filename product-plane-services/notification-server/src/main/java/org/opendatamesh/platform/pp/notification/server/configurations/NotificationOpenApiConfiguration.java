package org.opendatamesh.platform.pp.notification.server.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationOpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform Product Plane Notification Service API Reference")
                        .summary("This page describe tha API exposed by the Notification Service Server of the Product Plane " +
                                "of the Open Data Mesh Platform.")
                        .description(
                                "This page describe tha API exposed by the Notification Service Server of the Product Plane of the Open Data Mesh Platform." +

                                "\r\n# Overview" +
                                "\r\nThe Notification Module of the Open Data Mesh platform manages the lifecycle of Observers and the dispatch of Notification to them." +
                                "An Observer is an Adapter of the Notification API service in the Utility Plane." +
                                "\r\nIn addition to this, it also keeps track of every dispatched Notification and their status."
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
