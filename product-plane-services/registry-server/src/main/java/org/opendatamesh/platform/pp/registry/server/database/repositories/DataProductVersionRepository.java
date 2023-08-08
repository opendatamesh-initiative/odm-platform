package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.DataProductVersionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DataProductVersionRepository extends JpaRepository<DataProductVersion, DataProductVersionId>,
        JpaSpecificationExecutor<DataProductVersion> {

    List<DataProductVersion> findByDataProductId(String id);
    Optional<DataProductVersion> findByDataProductIdAndVersionNumber(String dataProductId, String versionNumber);
}
