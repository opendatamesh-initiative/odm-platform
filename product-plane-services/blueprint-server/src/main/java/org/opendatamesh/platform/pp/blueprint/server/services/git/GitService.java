package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;

import java.io.File;

public interface GitService {

    String targetPath = "/tmp";

    RepositoryProviderEnum getType();

    void createRepo();

    default Git cloneRepo(String sourceUrl) {
        try {
            return Git.cloneRepository()
                    .setURI(sourceUrl)
                    .setDirectory(new File(targetPath))
                    .call();
            // LOG CORRECT CLONE
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error cloning repository",
                    t
            );
        }
    }

    default Git changeOrigin(Git gitRepository, String newOrigin) {
        try {
            StoredConfig config = gitRepository.getRepository().getConfig();
            config.setString("remote", "origin", "url", newOrigin);
            config.save();
            return gitRepository;
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error changing origin to the Git repository",
                    t
            );
        }
    }

    default void commitAndPushRepo(Git gitRepo, String message) {
        try {
            gitRepo.commit().setMessage(message);
            gitRepo.push();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            );
        }
    }

}
