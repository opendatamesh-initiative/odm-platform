package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.opendatamesh.platform.pp.blueprint.server.utils.CustomFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public abstract class GitService {

    //TODO : timeouts on operations?

    @Autowired
    OAuthTokenManager oAuthTokenManager; // Works ONLY with AzureDevOps

    @Value("${git.templates.path}")
    private String templatesPath;

    private String targetPathSuffix = "/projects";

    private String sourcePathSuffix = "/blueprints";

    private String paramsFileJson = "params.json";


    // ======================================================================================
    // CREATE Repository
    // ======================================================================================

    public abstract void createRepo(String organization, String projectName, String repositoryName);


    // ======================================================================================
    // CHECK Repository content
    // ======================================================================================

    public GitCheckResource checkGitRepository(String repositoryUrl, String blueprintDirectory) {

        Git repoToCheck = cloneRepo(repositoryUrl);
        File repoToCheckFile = repoToCheck.getRepository().getWorkTree();

        GitCheckResource gitCheckResource = new GitCheckResource();

        gitCheckResource.setBlueprintDirectoryCheck(
                CustomFileUtils.existsAsDirectoryInDirectory(repoToCheckFile, blueprintDirectory)
        );
        gitCheckResource.setParamsDescriptionCheck(
                CustomFileUtils.existsAsFileInDirectory(repoToCheckFile, paramsFileJson)
        );

        if(gitCheckResource.getParamsDescriptionCheck()) {
            String paramsFileContent = CustomFileUtils.readFileAsString(new File(repoToCheckFile, paramsFileJson));
            gitCheckResource.setParamsJsonFileContent(paramsFileContent);
        }

        // Clean state
        repoToCheck.close();
        deleteLocalRepository();

        return gitCheckResource;

    }

    // ======================================================================================
    // CLONE Repository
    // ======================================================================================

    public Git cloneRepo(String sourceUrl) {
        return cloneRepo(sourceUrl, templatesPath + sourcePathSuffix);
    }

    private Git cloneRepo(String sourceUrl, String destinationPath) {
        try {
            if(isHttpsRemote(sourceUrl)) {
                return Git.cloneRepository()
                        .setURI(sourceUrl)
                        .setDirectory(new File(destinationPath))
                        .setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider("", oAuthTokenManager.getToken())
                        )
                        .call();
            } else {
                return Git.cloneRepository()
                        .setURI(sourceUrl)
                        .setDirectory(new File(destinationPath))
                        .setTransportConfigCallback(getSshTransportConfigCallback())
                        .call();
            }
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error cloning repository - " + t.getMessage(),
                    t
            );
        }
    }


    // ======================================================================================
    // INIT new Git repository
    // ======================================================================================

    public Git initTargetRepository(Git oldGitRepo, String blueprintDir, Boolean createRepoFlag, String targetOrigin) {
        try {
            Git newGitRepo = null;
            File oldRepo = oldGitRepo.getRepository().getWorkTree();
            if (createRepoFlag) {
                newGitRepo = Git.init()
                        .setDirectory(new File(templatesPath + targetPathSuffix))
                        .call();
                // Set origin to new Repo
                newGitRepo = setOrigin(newGitRepo, targetOrigin);
            } else {
                // Clone old repo
                try {
                    newGitRepo = cloneRepo(targetOrigin, templatesPath + targetPathSuffix);
                } catch(Throwable t) {
                    throw new InternalServerException(
                            BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                            "createRepo=false, but an error occured cloning existing repository ["
                                    + targetOrigin + "]. Error: " + t.getMessage()
                                    + ". Check if the repository exists.",
                            t
                    );
                }
                // Remove all repo content
                CustomFileUtils.cleanDirectoryExceptOneDir(
                        newGitRepo.getRepository().getWorkTree(),
                        ".git"
                );
            }
            // Copy the blueprint directory content of the old repo to the new repo
            File newRepoFile = newGitRepo.getRepository().getWorkTree();
            File blueprintDirectoryFile = new File(oldRepo, blueprintDir);
            CustomFileUtils.copyDirectory(blueprintDirectoryFile, newRepoFile);
            // Close Git connection to old repository
            oldGitRepo.close();
            // Return new Repo
            return newGitRepo;
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error preparing local repository for templating - " + t.getMessage(),
                    t
            );
        }
    }


    // ======================================================================================
    // COMMIT & PUSH Repository
    // ======================================================================================

    public Iterable<PushResult> commitAndPushRepo(Git gitRepo, String message) {
        try {
            // Remove renamed files/directories
            gitRepo.add()
                    .setUpdate(true)
                    .addFilepattern(".")
                    .call();
            // Add new templated files;
            gitRepo.add()
                    .addFilepattern(".")
                    .call();
            // Commit changes
            gitRepo.commit()
                    .setMessage(message)
                    .call();
            // Push changes
            Iterable<PushResult> pushResults;
            if(isHttpsRemote(gitRepo)) {
                pushResults = gitRepo.push()
                        .setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider("", oAuthTokenManager.getToken())
                        )
                        .call();
            } else {
                pushResults = gitRepo.push()
                        .setTransportConfigCallback(getSshTransportConfigCallback())
                        .call();
            }
            return pushResults; // Needed for Windows to wait the end of the push command
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project - " + t.getMessage(),
                    t
            );
        } finally {
            // Explicitly close Git connection (needed for Windows)
            gitRepo.close();
        }
    }


    // ======================================================================================
    // DELETE local Repository
    // ======================================================================================

    public void deleteLocalRepository() {
        CustomFileUtils.removeDirectory(new File(templatesPath));
    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private Git setOrigin(Git gitRepository, String origin) throws URISyntaxException, GitAPIException {
        gitRepository.remoteAdd()
                .setName("origin")
                .setUri(new URIish(origin))
                .call();
        return gitRepository;
    }

    /*private Git changeOrigin(Git gitRepository, String newOrigin) {
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
    }*/

    private Boolean isHttpsRemote(Git gitRepository) throws GitAPIException {
        List<RemoteConfig> remoteGitRepositoryConfigsList = gitRepository.remoteList().call();
        RemoteConfig remoteGitRepositoryConfigs = remoteGitRepositoryConfigsList.get(0);
        String remoteRepoUrl = remoteGitRepositoryConfigs.getURIs().get(0).toString();
        return isHttpsRemote(remoteRepoUrl);
    }

    private Boolean isHttpsRemote(String remoteRepoUrl) {
        return remoteRepoUrl.contains("https://");
    }

    private TransportConfigCallback getSshTransportConfigCallback() {
        return transport -> {
            if (transport instanceof SshTransport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(SshSessionFactory.getInstance());
            }
        };
    }

}