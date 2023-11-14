package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.URIish;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.opendatamesh.platform.pp.blueprint.server.utils.CustomFileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.net.URISyntaxException;

public abstract class GitService {

    //TODO : timeouts on operations?

    @Value("${git.templates.path}")
    private String targetPath;

    private String tmpTargetRepo = "tmpTargetRepo";

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
            // Remove exception from signature and refactor this method in a File utils class (used also in TemplatingService)
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
        return cloneRepo(sourceUrl, targetPath);
    }

    private Git cloneRepo(String sourceUrl, String destinationPath) {
        try {
            return Git.cloneRepository()
                    .setURI(sourceUrl)
                    .setDirectory(new File(destinationPath))
                    .setTransportConfigCallback(getSshTransportConfigCallback())
                    .call();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error cloning repository",
                    t
            );
        }
    }


    // ======================================================================================
    // INIT new Git repository
    // ======================================================================================

    public Git initTargetRepository(Git oldGitRepo, String blueprintDir, Boolean createRepoFlag, String targetOrigin) {
        try {
            Git newGitRepo;
            File oldRepo = oldGitRepo.getRepository().getWorkTree();
            if (createRepoFlag) {
                newGitRepo = Git.init()
                        .setDirectory(new File(tmpTargetRepo))
                        .call();
                // Set origin to new Repo
                newGitRepo = setOrigin(newGitRepo, targetOrigin);
            } else {
                // Clone old repo
                newGitRepo = cloneRepo(targetOrigin, tmpTargetRepo);
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
            // Remove the old repository
            oldGitRepo.close(); // Close git connection
            deleteLocalRepository();
            // Move new repo to "targetPath" as it was the old repo
            CustomFileUtils.renameFile(newRepoFile, targetPath);
            // Return new Repo
            return newGitRepo;
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error preparing local repository for templating",
                    t
            );
        }
    }


    // ======================================================================================
    // COMMIT & PUSH Repository
    // ======================================================================================

    public void commitAndPushRepo(Git gitRepo, String message) {
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
            Iterable<PushResult> pushResults = gitRepo.push()
                    .setTransportConfigCallback(getSshTransportConfigCallback())
                    .call();
            System.out.println(pushResults); // REMOVE IT
            gitRepo.close();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            );
        }
    }


    // ======================================================================================
    // DELETE local Repository
    // ======================================================================================

    public void deleteLocalRepository() {
        CustomFileUtils.removeDirectory(new File(targetPath));
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

    private TransportConfigCallback getSshTransportConfigCallback() {
        return transport -> {
            if (transport instanceof SshTransport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(SshSessionFactory.getInstance());
            }
        };
    }

}