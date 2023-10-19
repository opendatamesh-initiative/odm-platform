package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public class AzureDevOpsClient extends ODMClient {

    public AzureDevOpsClient() {
        super(
                "<to do>",
                ObjectMapperFactory.JSON_MAPPER
        );
    }

    public void createRemoteRepository(String repositoryName) {
        // TO DO
    }
}
