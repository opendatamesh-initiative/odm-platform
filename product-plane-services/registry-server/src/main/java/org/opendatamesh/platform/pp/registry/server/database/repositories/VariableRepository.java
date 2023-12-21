package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.variables.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableRepository extends JpaRepository<Variable, Long> {

    List<Variable> findByDataProductIdAndDataProductVersion(String dataProductId, String dataProductVersion);

}