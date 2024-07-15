package org.opendatamesh.platform.pp.blueprint.server.resources.internals;


public class GitCheckResource {

    private boolean blueprintDirectoryCheck;

    private boolean paramsDescriptionCheck;

    private String paramsJsonFileContent;

    public boolean isBlueprintDirectoryCheck() {
        return blueprintDirectoryCheck;
    }

    public void setBlueprintDirectoryCheck(boolean blueprintDirectoryCheck) {
        this.blueprintDirectoryCheck = blueprintDirectoryCheck;
    }

    public boolean isParamsDescriptionCheck() {
        return paramsDescriptionCheck;
    }

    public void setParamsDescriptionCheck(boolean paramsDescriptionCheck) {
        this.paramsDescriptionCheck = paramsDescriptionCheck;
    }

    public String getParamsJsonFileContent() {
        return paramsJsonFileContent;
    }

    public void setParamsJsonFileContent(String paramsJsonFileContent) {
        this.paramsJsonFileContent = paramsJsonFileContent;
    }
}
