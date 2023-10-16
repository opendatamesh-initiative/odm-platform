package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;

import java.io.File;
import java.io.IOException;

public interface GitService {

    //TODO : timeouts on operations?

    String targetPath = "tmp";

    RepositoryProviderEnum getType();

    void createRepo();

    default Git cloneRepo(String sourceUrl) {
        try {
            return Git.cloneRepository()
                    .setURI(sourceUrl)
                    .setDirectory(new File(targetPath))
                    .call();
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
            gitRepo.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider("mattia155", "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")).call();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            );
        }
    }

    default void deleteLocalRepository() {
        try {
            FileUtils.deleteDirectory(new File(targetPath));
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            ); // CHANGE IT
        }
    }

}
