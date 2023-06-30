package org.opendatamesh.platform.pp.registry.database.repositories;


import java.util.List;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship.ApiToSchemaRelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApiToSchemaRelationshipRepository extends JpaRepository<ApiToSchemaRelationship, Long>, JpaSpecificationExecutor<ApiToSchemaRelationship> {

    public List<ApiToSchemaRelationship> findByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByOperationId(String operationId);
    public boolean existsByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByIdApiId(Long apiId);
    public List<ApiToSchemaRelationship> findByIdSchemaId(Long schemaId);
    public List<ApiToSchemaRelationship> findById(ApiToSchemaRelationshipId relId);
}
