package org.opendatamesh.platform.core.commons.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.opendatamesh.dpds.location.GitService;
import org.opendatamesh.platform.core.commons.git.providers.github.GitHubClient;
import org.opendatamesh.platform.core.commons.git.resources.errors.GitStandardErrors;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.utils.CustomFileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

class GitHubService implements GitService {

    private GitHubClient gitHubClient;
    private final String personalAccessToken;
    private final String username;

    //TODO refactor --> Generic, GitHub and Azure only differs for the createRepo method, while the other method are the same
    public GitHubService(String username, String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
        this.username = username;
        this.gitHubClient = new GitHubClient(personalAccessToken);
    }

    @Override
    public void createRepo(String organization, String projectName, String repositoryName) {
        gitHubClient.createRemoteRepository(organization, repositoryName);
    }

    @Override
    public Git cloneRepo(
            String sourceUrl, String destinationPath, Boolean cloneAllBranches, List branchList, String branch
    ) {
        try {
            CloneCommand cloneCommand = configureCloneCommand(
                    sourceUrl, destinationPath, cloneAllBranches, branchList, branch
            );
            if (isHttpsRemote(sourceUrl)) {
                cloneCommand.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(this.username, personalAccessToken)
                );
            } else {
                cloneCommand.setTransportConfigCallback(getSshTransportConfigCallback());
            }
            return cloneCommand.call();
        } catch (Exception t) {
            throw new InternalServerException(
                    GitStandardErrors.SC500_01_GIT_ERROR,
                    "Error cloning repository - " + t.getMessage(),
                    t
            );
        }
    }

    @Override
    public Git initTargetRepository(
            Git oldGitRepo, String blueprintDir, Boolean createRepoFlag, String destinationPath, String targetOrigin
    ) {
        try {
            Git newGitRepo = null;
            File oldRepo = oldGitRepo.getRepository().getWorkTree();
            if (createRepoFlag) {
                newGitRepo = Git.init()
                        .setDirectory(new File(destinationPath))
                        .call();
                // Set origin to new Repo
                newGitRepo = setOrigin(newGitRepo, targetOrigin);
            } else {
                // Clone old repo
                try {
                    newGitRepo = cloneRepo(targetOrigin, destinationPath, false, null, null);
                } catch (Exception t) {
                    throw new InternalServerException(
                            GitStandardErrors.SC500_01_GIT_ERROR,
                            "createRepo=false, but an error occurred cloning existing repository ["
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
        } catch (Exception t) {
            throw new InternalServerException(
                    GitStandardErrors.SC500_01_GIT_ERROR,
                    "Error preparing local repository for templating - " + t.getMessage(),
                    t
            );
        }
    }

    @Override
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
            if (isHttpsRemote(gitRepo)) {
                pushResults = gitRepo.push()
                        .setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider(this.username, this.personalAccessToken)
                        )
                        .call();
            } else {
                pushResults = gitRepo.push()
                        .setTransportConfigCallback(getSshTransportConfigCallback())
                        .call();
            }
            return pushResults; // Needed for Windows to wait the end of the push command
        } catch (Exception t) {
            throw new InternalServerException(
                    GitStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project - " + t.getMessage(),
                    t
            );
        } finally {
            // Explicitly close Git connection (needed for Windows)
            gitRepo.close();
        }
    }

    @Override
    public void deleteLocalRepository(String localRepositoryPath) {
        CustomFileUtils.removeDirectory(new File(localRepositoryPath));
    }

    private CloneCommand configureCloneCommand(
            String sourceUrl, String destinationPath, Boolean cloneAllBranches, List branchList, String branch
    ) {
        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(sourceUrl)
                .setDirectory(new File(destinationPath));
        if (cloneAllBranches) {
            cloneCommand.setCloneAllBranches(cloneAllBranches);
            if (branchList != null && branch != null) {
                cloneCommand.setBranchesToClone(branchList);
                cloneCommand.setBranch(branch);
            }
        }
        return cloneCommand;
    }

    private Git setOrigin(Git gitRepository, String origin) throws URISyntaxException, GitAPIException {
        gitRepository.remoteAdd()
                .setName("origin")
                .setUri(new URIish(origin))
                .call();
        return gitRepository;
    }

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
