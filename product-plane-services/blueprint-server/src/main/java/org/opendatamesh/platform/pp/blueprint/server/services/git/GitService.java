package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;

public interface GitService {

    RepositoryProviderEnum getType();

    void createRepo();

    default void pullRepo() {

    }

    default void pushRepo() {

    }

}
