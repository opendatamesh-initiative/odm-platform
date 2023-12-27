package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.VariableResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.variables.Variable;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VariableMapper {

    Variable toEntity(VariableResource resource);

    VariableResource toResource(Variable entity);

    List<VariableResource> toResources(List<Variable> entities);

}
