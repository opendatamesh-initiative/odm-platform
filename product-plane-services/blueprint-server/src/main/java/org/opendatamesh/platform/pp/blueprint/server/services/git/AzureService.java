package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;
import org.springframework.stereotype.Component;

@Component
public class AzureService implements GitService {

    @Override
    public RepositoryProviderEnum getType() {
        return RepositoryProviderEnum.AZURE_DEVOPS;
    }

    @Override
    public void createRepo() {

    }
}
