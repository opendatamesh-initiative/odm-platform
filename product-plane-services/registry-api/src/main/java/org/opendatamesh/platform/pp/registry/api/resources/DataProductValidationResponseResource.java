package org.opendatamesh.platform.pp.registry.api.resources;

import java.util.HashMap;
import java.util.Map;

public class DataProductValidationResponseResource {

    private DataProductValidationResult syntaxValidationResult;

    private Map<String, DataProductValidationResult> policiesValidationResults = new HashMap<>();

    public DataProductValidationResult getSyntaxValidationResult() {
        return syntaxValidationResult;
    }

    public void setSyntaxValidationResult(DataProductValidationResult syntaxValidationResult) {
        this.syntaxValidationResult = syntaxValidationResult;
    }

    public Map<String, DataProductValidationResult> getPoliciesValidationResults() {
        return policiesValidationResults;
    }

    public void setPoliciesValidationResults(Map<String, DataProductValidationResult> policiesValidationResults) {
        this.policiesValidationResults = policiesValidationResults;
    }

    public static class DataProductValidationResult {
        private boolean validated;
        private Object validationOutput;
        private Boolean blockingFlag;

        public DataProductValidationResult() {
        }

        public DataProductValidationResult(boolean validated, Object validationOutput) {
            this.validated = validated;
            this.validationOutput = validationOutput;
        }

        public DataProductValidationResult(boolean validated, Object validationOutput, Boolean blockingFlag) {
            this.validated = validated;
            this.validationOutput = validationOutput;
            this.blockingFlag = blockingFlag;
        }

        public boolean isValidated() {
            return validated;
        }

        public void setValidated(boolean validated) {
            this.validated = validated;
        }

        public Object getValidationOutput() {
            return validationOutput;
        }

        public void setValidationOutput(Object validationOutput) {
            this.validationOutput = validationOutput;
        }

        public Boolean getBlockingFlag() {
            return blockingFlag;
        }

        public void setBlockingFlag(Boolean blockingFlag) {
            this.blockingFlag = blockingFlag;
        }
    }
}
