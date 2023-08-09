package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ApiDefinitionReference extends DefinitionReference {
    List<ApiDefinitionEndpoint> endpoints;
}
