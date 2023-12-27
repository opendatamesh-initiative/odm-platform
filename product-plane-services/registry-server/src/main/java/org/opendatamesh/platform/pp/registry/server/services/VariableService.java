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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VariableService {

    @Autowired
    private VariableRepository variableRepository;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^{}]+)\\}");

    private static final Logger logger = LoggerFactory.getLogger(VariableService.class);

    public Variable createVariable(
            String dataProductId, String versionNumber, String variableName, String variableValue
    ) {

        Variable variable = null;

        if(findVariables(dataProductId, versionNumber, variableName).size() == 0) {

            variable = new Variable();
            variable.setDataProductId(dataProductId);
            variable.setDataProductVersion(versionNumber);
            variable.setVariableName(variableName);
            variable.setVariableValue(variableValue);

            try {
                variable = saveVariable(variable);
                logger.info("Variable [" + variableName + "] of DPV ["
                        + dataProductId + " - " + versionNumber + "] successfully created");
            } catch(Throwable t) {
                throw new InternalServerException(
                        ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                        "An error occurred in the backend database while saving variable ["
                                + variableName + "] of DPV [" + dataProductId + " - " + versionNumber + "]",
                        t
                );
            }
        }

        return variable;
    }

    private Variable saveVariable(Variable variable) {
        return variableRepository.saveAndFlush(variable);
    }

    public Variable updateVariable(Long variableId, String variableValue) {
        try {
            Variable variable = loadVariable(variableId);
            variable.setVariableValue(variableValue);
            variable = saveVariable(variable);
            return variable;
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while updating variable [" + variableId + "]",
                    t
            );
        }
    }

    public Boolean variableExists(Long id) {
        return loadVariable(id) != null;
    }

    private Variable loadVariable(Long id) {

        Variable variable = null;

        Optional<Variable> findResult = variableRepository.findById(id);
        if(findResult.isPresent()) {
            variable = findResult.get();
        }

        return variable;
    }

    public List<Variable> searchVariables(String dataProductId, String versionNumber) {
        try {
            return findVariables(dataProductId, versionNumber, null);
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while searching variable of DPV ["
                            + dataProductId + " - " + versionNumber + "]",
                    t
            );
        }
    }

    private List<Variable> findVariables(String dataProductId, String versionNumber, String variableName) {
        return variableRepository.findAll(
                VariableRepository.Specs.hasMatch(
                        dataProductId,
                        versionNumber,
                        variableName
                )
        );
    }

    public void searchAndSaveVariablesFromDescriptor(
            String descriptorRawContent, String dataProductId, String versionNumber
    ) {
        Matcher matcher = VARIABLE_PATTERN.matcher(descriptorRawContent);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            createVariable(
                    dataProductId,
                    versionNumber,
                    variableName,
                    null
            );
        }
    }

    public String replaceVariables(
            String descriptorRawContent, String dataProductId, String versionNumber
    ) {
        List<Variable> variables = findVariables(dataProductId, versionNumber, null);
        for(Variable variable : variables) {
            if(variable.getVariableValue() != null) {
                descriptorRawContent = descriptorRawContent.replace(
                        "${" + variable.getVariableName() + "}",
                        variable.getVariableValue()
                );
            }
        }
        return descriptorRawContent;
    }

}
