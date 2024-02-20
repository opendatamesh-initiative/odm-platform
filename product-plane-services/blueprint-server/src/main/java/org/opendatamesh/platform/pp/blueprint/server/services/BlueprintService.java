package org.opendatamesh.platform.pp.blueprint.server.services;

import org.eclipse.jgit.api.Git;
import org.opendatamesh.platform.core.commons.git.GitService;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.opendatamesh.platform.pp.blueprint.server.database.repositories.BlueprintRepository;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BlueprintService {

    @Autowired
    BlueprintRepository blueprintRepository;

    @Autowired
    TemplatingService templatingService;

    @Autowired
    GitCheckerService gitCheckerService;

    @Autowired
    GitService gitService;

    @Value("${git.templates.path}")
    private String templatesPath;

    @Value("${git.provider}")
    private String gitProvider; // Remove it when GitHub will support OAuth2

    private String targetPathSuffix = "/projects";

    private String sourcePathSuffix = "/blueprints";

    private static final Logger logger = LoggerFactory.getLogger(BlueprintService.class);


    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Blueprint createBlueprint(Blueprint blueprint, Boolean checkBlueprint) throws IOException {

        if (blueprint == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_01_BLUEPRINT_IS_EMPTY,
                    "Blueprint object cannot be null");
        }

        if (blueprint.getRepositoryUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintDirectory() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint directory cannot be null"
            );
        }

        List<Blueprint> blueprints = searchBlueprints(
                blueprint.getRepositoryUrl(),
                blueprint.getBlueprintDirectory()
        );

        if (blueprints != null && blueprints.isEmpty() == false) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_02_BLUEPRINT_ALREADY_EXISTS,
                    "Blueprint [" + blueprint.getName() + "] of repo ["
                            + blueprint.getRepositoryUrl() + "] already exist"
            );
        }

        if (checkBlueprint) {
            GitCheckResource gitCheckResource = gitCheckerService.checkGitRepository(
                    blueprint.getRepositoryUrl(),
                    blueprint.getBlueprintDirectory(),
                    templatesPath
            );

            if (!gitCheckResource.getBlueprintDirectoryCheck()) {
                throw new UnprocessableEntityException(
                        BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                        "Missing blueprintDirectory [" + blueprint.getBlueprintDirectory() + "] in the given repository"
                );
            }

            if (!gitCheckResource.getParamsDescriptionCheck()) {
                throw new UnprocessableEntityException(
                        BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                        "Missing file [params.json] in the given repository"
                );
            }

            blueprint.setBlueprintParams(gitCheckResource.getParamsJsonFileContent());
        }

        try {
            blueprint = saveBlueprint(blueprint);
            logger.info(
                    "Blueprint [" + blueprint.getName() + "] "
                    + "of version [" + blueprint.getVersion() + "] "
                    + "of repository [" + blueprint.getRepositoryUrl() + "/" + blueprint.getBlueprintDirectory() + "] "
                    + "succesfully registered"
            );
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving blueprint [" + blueprint.getName() + "] "
                            + "of repository [" + blueprint.getRepositoryUrl() + "]",
                    t
            );
        }

        return blueprint;
    }

    private Blueprint saveBlueprint(Blueprint blueprint) {
        return blueprintRepository.saveAndFlush(blueprint);
    }


    // ======================================================================================
    // READ ALL
    // ======================================================================================

    public List<Blueprint> readBlueprints() {
        try {
            return blueprintRepository.findAll();
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading blueprints",
                    t
            );
        }
    }


    // ======================================================================================
    // READ ONE
    // ======================================================================================

    public Blueprint readOneBlueprint(Long blueprintId) {

        Blueprint blueprint = null;

        try {
            blueprint = loadBlueprint(blueprintId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading blueprint with id [" + blueprintId + "]",
                    t
            );
        }

        if (blueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id equals to [" + blueprintId + "] does not exist"
            );
        }
        return blueprint;
    }

    private Blueprint loadBlueprint(Long blueprintId) {
        Optional<Blueprint> blueprintLookUpResult = blueprintRepository.findById(blueprintId);
        if (blueprintLookUpResult.isPresent())
            return blueprintLookUpResult.get();
        else
            return null;
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Blueprint updateBlueprint(Long blueprintId, Blueprint blueprint) {

        if(blueprint == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_01_BLUEPRINT_IS_EMPTY,
                    "Blueprint object cannot be null");
        }

        if (blueprint.getRepositoryUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintDirectory() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint directory cannot be null"
            );
        }

        if(!blueprintRepository.existsById(blueprintId)) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        blueprint.setId(blueprintId);

        try {
            blueprint = saveBlueprint(blueprint);
            logger.info("Blueprint with id [" + blueprint.getId() + "] succesfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating blueprint with id [" + blueprint.getId() + "]",
                    t
            );
        }

        return blueprint;
    }


    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteBlueprint(Long blueprintId) {

        if(!blueprintRepository.existsById(blueprintId)) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        try {
            blueprintRepository.deleteById(blueprintId);
            logger.info("Blueprint with id [" + blueprintId + "] succesfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting blueprint with id [" + blueprintId + "]",
                    t
            );
        }
    }


    // ======================================================================================
    // SEARCH
    // ======================================================================================

    private List<Blueprint> searchBlueprints(String repositoryUrl, String blueprintDirectory) {
        try {
            return blueprintRepository.findAll(
                    BlueprintRepository.Specs.hasMatch(repositoryUrl, blueprintDirectory)
            );
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching blueprints",
                    t
            );
        }
    }


    // ======================================================================================
    // INSTANCE
    // ======================================================================================

    public void instanceBlueprint(Long blueprintId, ConfigResource configResource) {

        if(configResource == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_02_CONFIG_IS_EMPTY,
                    "Config object cannot be null when performing INSTANCE of a blueprint");
        }

        if(configResource.getTargetRepo() == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_03_CONFIG_IS_INVALID,
                    "Target Repository of Config object cannot be null when performing INSTANCE of a blueprint");
        }

        if(configResource.getConfig() == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_03_CONFIG_IS_INVALID,
                    "Config sections of Config object cannot be null when performing INSTANCE of a blueprint");
        }

        Blueprint blueprint = loadBlueprint(blueprintId);

        if(blueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        try {

            // Clone the BLUEPRINT repository
            logger.info("Cloning repository [" + blueprint.getRepositoryUrl() + "] ...");
            Git gitRepo = gitService.cloneRepo(
                    blueprint.getRepositoryUrl(),
                    templatesPath + sourcePathSuffix
            );
            logger.info("Repository [" + blueprint.getRepositoryUrl() + "] correctly cloned");

            // Clean the repository to consider only the template
            logger.info("Initializing new Git target repository ...");
            gitRepo = gitService.initTargetRepository(
                    gitRepo,
                    blueprint.getBlueprintDirectory(),
                    configResource.getCreateRepo(),
                    templatesPath + targetPathSuffix,
                    blueprint.getRepoBaseUrl() + configResource.getTargetRepo()
            );
            logger.info("Target repository initialized");

            // Get the working directory of the repository and call the templatingService to instance the BLUEPRINT
            logger.info("Templating the repository ...");
            File workingDirectory = gitRepo.getRepository().getWorkTree();
            templatingService.templating(workingDirectory, configResource);
            logger.info("Repository correctly templated");

            if (configResource.getCreateRepo()) {
                logger.info("Creating the target repository  on the remote provider ...");
                // Create the targetRepo
                gitService.createRepo(
                        blueprint.getOrganization(),
                        blueprint.getProjectId(),
                        configResource.getTargetRepo()
                );
            } else {
                logger.info("Repository creation skipped (createRepo=false)");
            }

            logger.info("Committing and pushing the repository ...");
            // Commit and Push the project created from the BLUEPRINT
            gitService.commitAndPushRepo(
                    gitRepo,
                    "Project initialization from blueprint [" + blueprint.getRepositoryUrl() + "]"
            );
            logger.info("Repository correctly pushed");

            // Delete local repository
            gitService.deleteLocalRepository(templatesPath);

        } catch (Throwable t) {
            // Delete local repository
            gitService.deleteLocalRepository(templatesPath);
            throw t;
        }
    }

}
