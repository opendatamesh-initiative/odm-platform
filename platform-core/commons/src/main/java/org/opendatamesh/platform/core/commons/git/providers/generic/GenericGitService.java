package org.opendatamesh.platform.core.commons.git.providers.generic;

import org.opendatamesh.platform.core.commons.git.GitService;

public class GenericGitService extends GitService {

    @Override
    public void createRepo(String organization, String projectName, String repositoryName) {
        throw new UnsupportedOperationException();
    }

}