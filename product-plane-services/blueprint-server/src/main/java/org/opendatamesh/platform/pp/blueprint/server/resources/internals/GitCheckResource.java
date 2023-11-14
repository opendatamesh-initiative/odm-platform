package org.opendatamesh.platform.pp.blueprint.server.resources.internals;

import lombok.Data;

@Data
public class GitCheckResource {

    private Boolean blueprintDirectoryCheck;

    private Boolean paramsDescriptionCheck;

    private String paramsJsonFileContent;

}
