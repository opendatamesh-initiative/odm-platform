package org.opendatamesh.platform.pp.devops.server.database.repositories;

import org.opendatamesh.platform.pp.devops.server.database.entities.Lifecycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LifecycleRepository extends JpaRepository<Lifecycle, Long>, JpaSpecificationExecutor<Lifecycle> {

    List<Lifecycle> findByDataProductId(String dataProductId);

    List<Lifecycle> findByDataProductIdAndDataProductVersion(String dataProductId, String versionNumber);

    Lifecycle findByDataProductIdAndDataProductVersionAndFinishedAtIsNull(String dataProductId, String versionNumber);

    
}
