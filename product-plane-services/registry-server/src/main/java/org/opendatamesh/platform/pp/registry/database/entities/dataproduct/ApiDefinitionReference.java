package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

import java.util.List;

@Data
public class ApiDefinitionReference extends DefinitionReference {
    List<ApiDefinitionEndpoint> endpoints;
}
