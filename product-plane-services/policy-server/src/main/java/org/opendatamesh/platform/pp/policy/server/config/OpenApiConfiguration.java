package org.opendatamesh.platform.pp.policy.server.config;

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
                .title("ODM Platform Product Plane Policy Service API Reference")
                .summary("This page describe tha API exposed by the Policy Service Server of the Product Plane " +
                        "of the Open Data Mesh Platform.")
                .description(
                    "# TODO"
                )
                .version("v1.0.0")
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
