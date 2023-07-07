package org.opendatamesh.platform.up.policy.server.api.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.policy.api.v1.resources.SuiteResource;
import org.opendatamesh.platform.up.policy.server.database.entities.SuiteEntity;

@Mapper(componentModel = "spring")
public interface  SuiteMapper {

    SuiteResource toResource(SuiteEntity suite);
    SuiteEntity toEntity(SuiteResource suite);
    Iterable<SuiteEntity> toEntity(Iterable<SuiteResource> suites);
    Iterable<SuiteResource> toResource(Iterable<SuiteEntity> suites);

}
