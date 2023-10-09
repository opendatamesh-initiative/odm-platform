package org.opendatamesh.platform.pp.blueprint.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public class BlueprintClient extends ODMClient {

    public BlueprintClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

}
