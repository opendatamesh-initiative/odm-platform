package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public abstract class GitService {

    //TODO : timeouts on operations?

    @Value("${git.templates.path}")
    private String targetPath;


    public abstract void createRepo(String repositoryName);

    public Git cloneRepo(String sourceUrl) {
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

    public Git changeOrigin(Git gitRepository, String newOrigin) {
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

    public void commitAndPushRepo(Git gitRepo, String message) {
        try {
            gitRepo.commit().setMessage(message);
            //gitRepo.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider("user", "token")).call();
            gitRepo.push().call();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            );
        }
    }

    public void deleteLocalRepository() {
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
