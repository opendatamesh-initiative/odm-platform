package org.opendatamesh.platform.pp.registry.database.repositories;

import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataProductRepository extends JpaRepository<DataProduct, String>, JpaSpecificationExecutor<DataProduct> {
}
