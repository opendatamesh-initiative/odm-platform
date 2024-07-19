package org.opendatamesh.platform.pp.params.server.database.repositories;

import org.opendatamesh.platform.pp.params.server.database.entities.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ParamRepository extends JpaRepository<Param, Long>, JpaSpecificationExecutor<Param> {

    List<Param> findByParamName(String paramName);

}
