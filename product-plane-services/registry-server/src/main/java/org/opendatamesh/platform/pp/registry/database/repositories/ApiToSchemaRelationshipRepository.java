package org.opendatamesh.platform.pp.registry.database.repositories;


import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApiToSchemaRelationshipRepository extends JpaRepository<ApiToSchemaRelationship, Long>, JpaSpecificationExecutor<ApiToSchemaRelationship> {

    public boolean findByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
    public boolean findByOperationId(String operationId);
    public boolean existsByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
}
