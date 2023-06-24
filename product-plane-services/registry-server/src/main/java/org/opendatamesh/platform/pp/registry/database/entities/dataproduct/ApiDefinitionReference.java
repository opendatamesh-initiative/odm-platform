package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import java.util.List;

import lombok.Data;

@Data
public class ApiDefinitionReference extends DefinitionReference {
    List<ApiDefinitionEndpoint> endpoints;
}
