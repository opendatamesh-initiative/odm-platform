package org.opendatamesh.platform.pp.params.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.opendatamesh.platform.pp.params.server.database.entities.Param;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParamMapper {

    ParamResource toResource(Param param);
    Param toEntity(ParamResource param);
    List<ParamResource> toResources(List<Param> params);

}
