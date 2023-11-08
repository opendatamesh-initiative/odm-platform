package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GitService {

    //TODO : timeouts on operations?

    @Value("${git.templates.path}")
    private String targetPath;

    public abstract void createRepo(String organization, String projectName, String repositoryName);

    public Git cloneRepo(String sourceUrl) {
        try {
            return Git.cloneRepository()
                    .setURI(sourceUrl)
                    .setDirectory(new File(targetPath))
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
            gitRepo.push()
                    .setTransportConfigCallback(getSshTransportConfigCallback())
                    .call();
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error committing and pushing the project",
                    t
            );
        }
    }

    public void cleanLocalRepository(Git gitRepo, String blueprintDirectory) {
        try {
            // Get working tree
            File dirToClean = gitRepo.getRepository().getWorkTree();
            // Define which sub-dir maintain (.git and the blueprint directory)
            List<String> subDirsToPreserve = new ArrayList<>();
            subDirsToPreserve.add(".git");
            subDirsToPreserve.add(blueprintDirectory);
            // Remove undesired additional content
            for (File file : dirToClean.listFiles()) {
                if(!subDirsToPreserve.contains(file.getName())) {
                    if(file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            // Move the blueprint directory content to the root content of the repo
            File blueprintDirectoryFile = new File(dirToClean, blueprintDirectory);
            moveContentFromDirToDir(blueprintDirectoryFile, dirToClean);
            // Remove the noew empty blueprint directory
            FileUtils.deleteDirectory(blueprintDirectoryFile);
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error preparing local repository for templating",
                    t
            );
        }
    }

    public void moveContentFromDirToDir(File sourceDir, File targetDir) throws IOException {
        for (File file : sourceDir.listFiles()) {
            if (file.isDirectory())
                FileUtils.moveDirectoryToDirectory(file, targetDir, false);
            else
                file.renameTo(new File(targetDir, file.getName()));
        }
    }

    public void deleteLocalRepository() {
        try {
            FileUtils.deleteDirectory(new File(targetPath));
        } catch (Throwable t) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error deleting local repository",
                    t
            );
        }
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
