package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.variables.Variable;
import org.opendatamesh.platform.pp.registry.server.database.repositories.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VariableService {

    @Autowired
    private VariableRepository variableRepository;

    private static final Logger logger = LoggerFactory.getLogger(VariableService.class);

    public Variable createVariable(
            String dataProductId, String dataProductVersion, String variableName, String variableValue
    ) {

        Variable variable = new Variable();
        variable.setDataProductId(dataProductId);
        variable.setDataProductVersion(dataProductVersion);
        variable.setDataProductId(variableName);
        variable.setDataProductId(variableValue);

        try {
            variable = saveVariable(variable);
            logger.info("Variable [" + variableName + "] of DPV ["
                    + dataProductId + " - " + dataProductVersion + "] successfully created");
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while saving variable [" + variableName + "] of DPV ["
                            + dataProductId + " - " + dataProductVersion + "]",
                    t);
        }
        return variable;
    }

    private Variable saveVariable(Variable variable) {
        return variableRepository.saveAndFlush(variable);
    }

    public Variable updateVariable(Long variableId, String variableValue) {
        try {
            Variable variable = loadVariable(variableId);
            if(variable != null) {
                variable.setVariableValue(variableValue);
                variable = saveVariable(variable);
                return variable;
            } else {
                return null; // THROW AN ERROR OR SOMETHING BETTER THAN THIS
            }
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while updating variable [" + variableId + "]",
                    t
            );
        }
    }

    private Variable loadVariable(Long id) {

        Variable variable = null;

        Optional<Variable> findResult = variableRepository.findById(id);
        if(findResult.isPresent()) {
            variable = findResult.get();
        }

        return variable;
    }

    public List<Variable> searchVariables(String dataProductId, String dataProductVersion) {
        try {
            return variableRepository.findByDataProductIdAndDataProductVersion(dataProductId, dataProductVersion);
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while searching variable of DPV ["
                            + dataProductId + " - " + dataProductVersion + "]",
                    t
            );
        }
    }

}
