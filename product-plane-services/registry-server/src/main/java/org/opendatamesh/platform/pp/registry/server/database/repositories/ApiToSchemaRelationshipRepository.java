package org.opendatamesh.platform.pp.registry.server.database.repositories;


import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship.ApiToSchemaRelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApiToSchemaRelationshipRepository extends JpaRepository<ApiToSchemaRelationship, Long>, JpaSpecificationExecutor<ApiToSchemaRelationship> {

    public List<ApiToSchemaRelationship> findByIdApiIdAndIdSchemaId(String apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByOperationId(String operationId);
    public boolean existsByIdApiIdAndIdSchemaId(String apiId, Long schemaId);
    public List<ApiToSchemaRelationship> findByIdApiId(String apiId);
    public List<ApiToSchemaRelationship> findByIdSchemaId(Long schemaId);
    public List<ApiToSchemaRelationship> findById(ApiToSchemaRelationshipId relId);
}
