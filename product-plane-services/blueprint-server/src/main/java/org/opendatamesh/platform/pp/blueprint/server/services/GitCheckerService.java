package org.opendatamesh.platform.pp.blueprint.server.services;

import org.eclipse.jgit.api.Git;
import org.opendatamesh.dpds.location.GitService;
import org.opendatamesh.platform.core.commons.utils.CustomFileUtils;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class GitCheckerService {

    @Autowired
    GitService gitService;

    private String paramsFileJson = "params.json";


    // ======================================================================================
    // CHECK Repository content
    // ======================================================================================

    public GitCheckResource checkGitRepositoryAndLoadParams(
            String repositoryUrl,
            String blueprintDirectory,
            String destinationPath
    ) {
        try (Git repoToCheck = gitService.cloneRepo(repositoryUrl, destinationPath, false, null, null)) {
            File repoToCheckFile = repoToCheck.getRepository().getWorkTree();

            GitCheckResource gitCheckResource = new GitCheckResource();

            gitCheckResource.setBlueprintDirectoryCheck(
                    CustomFileUtils.existsAsDirectoryInDirectory(repoToCheckFile, blueprintDirectory)
            );
            String paramsFileJsonPath = blueprintDirectory + File.separator + paramsFileJson;
            gitCheckResource.setParamsDescriptionCheck(
                    CustomFileUtils.existsAsFileInDirectory(repoToCheckFile, paramsFileJsonPath)
            );

            if (gitCheckResource.isParamsDescriptionCheck()) {
                String paramsFileContent = CustomFileUtils.readFileAsString(new File(repoToCheckFile, paramsFileJsonPath));
                gitCheckResource.setParamsJsonFileContent(paramsFileContent);
            }
            return gitCheckResource;
        } finally {
            // Clean state
            gitService.deleteLocalRepository(destinationPath);
        }
    }

}