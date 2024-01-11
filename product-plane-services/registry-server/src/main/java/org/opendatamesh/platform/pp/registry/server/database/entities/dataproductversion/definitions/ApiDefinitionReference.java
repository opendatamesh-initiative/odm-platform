package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.definitions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.DefinitionReference;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ApiDefinitionReference extends DefinitionReference {
    List<ApiDefinitionEndpoint> endpoints;
}
