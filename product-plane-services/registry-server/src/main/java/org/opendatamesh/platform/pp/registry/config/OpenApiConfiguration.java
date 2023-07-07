package org.opendatamesh.platform.pp.registry.config;

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
    /*
     * @Bean
     * public GroupedOpenApi publicApi() {
     * return GroupedOpenApi.builder()
     * .group("springshop-public")
     * .pathsToMatch("/**")
     * .build();
     * }
     */
    
   
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("ODM Platform's data product experience plane API Reference")
                .summary("This page describe tha API exposed by the data produc experience " + 
                        "plane of the Open Data Mesh Platform.")
                .description(
                    "# Content Types" + 
                    "\r\nThe Schema Registry REST server uses content types for both requests and responses to indicate the serialization format of the data as well as the version of the API being used. Currently, the only serialization format supported is JSON and the only version of the API is `v1`. However, to remain compatible with future versions, you should specify preferred content types in requests and check the content types of responses."+
                    "\r\nThe preferred format for content types is `application/vnd.odmp.v1+json`, where `v1` is the API version and json is the serialization format. However, other less specific content types are permitted, including `application/vnd.odmp+json` to indicate no specific API version should be used (the most recent stable version will be used), `application/json`, and `application/octet-stream`. The latter two are only supported for compatibility and ease of use." + 
                    "\r\nYour requests should specify the most specific format and version information possible via the HTTP `Accept` header:" +
                    "\r\n```" +
                    "\r\nAccept: application/vnd.odmp.v1+json" + 
                    "\r\n```" + 
                    "\r\nThe server does not supports at the moment content negotiation." + 
                    "\r\n# Errors" +
                    "\r\nAll API endpoints use a standard error message format for any requests that return an HTTP status indicating an error (any 400 or 500 statuses). For example, a request entity that omits a required field may generate the following response:" +
                    "\r\n```json" +
                    "\r\nHTTP/1.1 422 Unprocessable Entity"+
                    "\r\nContent-Type: application/vnd.schemaregistry.v1+json"+
                    "\r\n{"+
                    "\r\n\"error_code\": 422,"+
                    "\r\n\"message\": \"schema may not be empty\""+
                    "\r\n}"+
                    "\r\n```" + 
                    "\r\nAlthough it is good practice to check the status code, you may safely parse the response of any non-DELETE API calls and check for the presence of an `error_code` field to detect errors." +
                    ""
                    )
                .version("v0.0.1")
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
