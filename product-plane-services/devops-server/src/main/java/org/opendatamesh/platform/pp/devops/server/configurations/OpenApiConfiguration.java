package org.opendatamesh.platform.pp.devops.server.configurations;

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
                .title("ODM Platform's DevOps API Reference")
                .summary("This page describe tha API exposed by ODM Platform's DevOps Service")
                .description(
                    "This page describe tha API exposed by ODM Platform's DevOps Service" + 
                    
                    "\r\n# Overview" + 
                    "\r\nThe DevOps Module of the Open Data Mesh platform manages the data product's lifecycle. Specifically, it handles the necessary activities to execute transitions between different stages within the lifecycle. The possible stages and the activities to be performed are described in the 'lifecycle' block of the data product descriptor. To perform a stage transition, it's required to create a new `activity`, specifying the version of the data product on which it should be executed and the target stage. " +
                    "\r\nThe DevOps Module then reads the definition of the data product version from the Registry Module, verifies that the target stage exists among those defined in the `lifecycle` block, and finally creates a plan of `tasks` to be executed to bring the data product to the desired stage. The activity and its associated tasks are created with a default state of `PLANNED`. " + 
                    "\r\nWhen the activity is initiated, the DevOps Module reads the next task to be executed in the execution plan and submits it to the execution service adapter specified in the data product descriptor, passing the `template` and `configurations` as input. When the adapter completes the task execution, it notifies the DevOps Module of the completion and the status, which then proceeds with executing the next task if available, or terminates the activity."+
                    

                    "\r\n# Content Types" + 
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
                    "\r\n\"status\": 404,"+
                    "\r\n\"code\": 40401,"+
                    "\r\n\"description\": \"Activity not found\""+
                    "\r\n\"message\": \"Activity with id equals to [5] does not exist\""+
                    "\r\n\"path\": \"/api/v1/pp/devops/activities/5\""+
                    "\r\n}"+
                    "\r\n```" + 
                    "\r\nAlthough it is good practice to check the status code, you may safely parse the response of any non-DELETE API calls and check for the presence of a `code` field to detect errors." +
                    "\r\nFor each endpoints all possible error codes, grouped by http response status, are listed and described with the exception of the following ones that are always possible:" +
                    "\r\n1. `40000` (Request body is not readable): this error is returned when" +
                    "\r\n1. `41501` (Request media type not supported): : this error is returned when" +
                    "\r\n1. `40601` (Request accepted media types not supported): : this error is returned when" +
                    ""
                    )

                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("https://github.com/opendatamesh-initiative/odm-platform/blob/main/LICENSE"))
                .contact(new Contact()
                    .name("ODM Platform Team")
                    .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                    .description("Open Data Mesh Platform's DevOps API Documentation")
                    .url("https://dpds.opendatamesh.org/")
                );
    }

}
