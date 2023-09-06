package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.definitions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.DefinitionReference;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ApiDefinitionReference extends DefinitionReference {
    List<ApiDefinitionEndpoint> endpoints;
}
