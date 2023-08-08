package org.opendatamesh.platform.pp.registry.server.database.repositories;


import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.ApiToSchemaRelationship.ApiToSchemaRelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApiToSchemaRelationshipRepository extends JpaRepository<ApiToSchemaRelationship, Long>, JpaSpecificationExecutor<ApiToSchemaRelationship> {

    public List<ApiToSchemaRelationship> findByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByOperationId(String operationId);
    public boolean existsByIdApiIdAndIdSchemaId(Long apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByIdApiId(Long apiId);
    public List<ApiToSchemaRelationship> findByIdSchemaId(Long schemaId);
    public List<ApiToSchemaRelationship> findById(ApiToSchemaRelationshipId relId);
}
