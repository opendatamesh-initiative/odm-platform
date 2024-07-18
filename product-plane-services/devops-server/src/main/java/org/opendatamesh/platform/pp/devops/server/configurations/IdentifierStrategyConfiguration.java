package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.dpds.parser.IdentifierStrategy;
import org.opendatamesh.dpds.parser.IdentifierStrategyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentifierStrategyConfiguration {
    @Value("${odm.organization.name:org.opendatamesh}")
    private String organizationName;

    @Bean
    IdentifierStrategy identifierStrategy() {
        return IdentifierStrategyFactory.getDefault(organizationName);
    }
}

