package org.opendatamesh.platform.pp.blueprint.server.services;

import org.eclipse.jgit.api.Git;
import org.opendatamesh.platform.core.commons.git.GitService;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.opendatamesh.platform.pp.blueprint.server.utils.CustomFileUtils;
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

    public GitCheckResource checkGitRepository(
            String repositoryUrl, String blueprintDirectory, String destinationPath
    ) {

        Git repoToCheck = gitService.cloneRepo(repositoryUrl, destinationPath);
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
        gitService.deleteLocalRepository(destinationPath);

        return gitCheckResource;

    }

}