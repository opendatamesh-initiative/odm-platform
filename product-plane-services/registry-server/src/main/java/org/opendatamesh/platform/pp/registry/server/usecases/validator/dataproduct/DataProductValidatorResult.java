package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

public class DataProductValidatorResult {
    private String name;
    private boolean validated;
    private Object validationOutput;
    private Boolean blockingFlag;

    public DataProductValidatorResult() {
    }

    public DataProductValidatorResult(String name, boolean validated, Object validationOutput, Boolean blockingFlag) {
        this.name = name;
        this.validated = validated;
        this.validationOutput = validationOutput;
        this.blockingFlag = blockingFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
