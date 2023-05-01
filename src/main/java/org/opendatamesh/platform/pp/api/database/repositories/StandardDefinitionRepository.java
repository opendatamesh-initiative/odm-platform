package org.opendatamesh.platform.pp.api.database.repositories;

import org.opendatamesh.platform.pp.api.database.entities.dataproduct.StandardDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StandardDefinitionRepository extends JpaRepository<StandardDefinition, Long>, JpaSpecificationExecutor<StandardDefinition> {
}
