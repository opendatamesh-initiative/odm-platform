package org.opendatamesh.platform.pp.blueprint.server.services;

import org.eclipse.jgit.api.Git;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.opendatamesh.platform.pp.blueprint.server.database.repositories.BlueprintRepository;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class BlueprintService {

    @Autowired
    BlueprintRepository blueprintRepository;

    @Autowired
    TemplatingService templatingService;

    @Autowired
    GitService gitService;

    private static final Logger logger = LoggerFactory.getLogger(BlueprintService.class);


    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Blueprint createBlueprint(Blueprint blueprint) {

        if (blueprint == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_01_BLUEPRINT_IS_EMPTY,
                    "Blueprint object cannot be null");
        }

        if (blueprint.getRepositoryBaseUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintRepo() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint path inside repository cannot be null"
            );
        }

        List<Blueprint> blueprints = searchBlueprints(
                blueprint.getRepositoryBaseUrl(),
                blueprint.getBlueprintRepo()
        );

        if (blueprints != null && blueprints.isEmpty() == false) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_02_BLUEPRINT_ALREADY_EXISTS,
                    "Blueprint [" + blueprint.getName() + "] in repo ["
                            + blueprint.getRepositoryBaseUrl()
                            + blueprint.getBlueprintRepo() + "] already exist"
            );
        }

        try {
            blueprint = saveBlueprint(blueprint);
            logger.info(
                    "Blueprint [" + blueprint.getName() + "] "
                    + "of version [" + blueprint.getVersion() + "] "
                    + "of repository [" + blueprint.getRepositoryBaseUrl() +  blueprint.getBlueprintRepo() + "] "
                    + "succesfully registered"
            );
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving blueprint [" + blueprint.getName() + "] "
                            + "of repository [" + blueprint.getRepositoryBaseUrl() + blueprint.getBlueprintRepo() + "]",
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
                    "An error occured in the backend database while loading activity with id [" + blueprintId + "]",
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

        if (blueprint.getRepositoryBaseUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintRepo() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint path inside repository cannot be null"
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

    private List<Blueprint> searchBlueprints(String repositoryBaseUrl, String blueprintRepo) {
        try {
            return blueprintRepository.findAll(
                    BlueprintRepository.Specs.hasMatch(repositoryBaseUrl, blueprintRepo)
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
    // INIT
    // ======================================================================================

    public void initBlueprint(Long blueprintId, ConfigResource configResource) {

        if(configResource == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_02_CONFIG_IS_EMPTY,
                    "Config object cannot be null when performing INIT of a blueprint");
        }

        Blueprint blueprint = loadBlueprint(blueprintId);

        if(blueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        try {

            // Clone the BLUEPRINT repository
            logger.info("Cloning repository [" + blueprint.getRepositoryBaseUrl() + blueprint.getBlueprintRepo() + "] ...");
            Git gitRepo = gitService.cloneRepo(blueprint.getRepositoryBaseUrl()+blueprint.getBlueprintRepo());
            logger.info(
                    "Repository ["
                            + blueprint.getRepositoryBaseUrl() + blueprint.getBlueprintRepo()
                            + "] correctly cloned"
            );

            // Get the working directory of the repository and call the templatingService to init the BLUEPRINT
            logger.info("Templating the repository ...");
            File workingDirectory = gitRepo.getRepository().getWorkTree();
            templatingService.templating(workingDirectory, configResource);
            logger.info("Repository correctly templated");

            logger.info("Creating the target repository and pushing the project initialized from blueprint ...");
            // Create the targetRepo
            gitService.createRepo(blueprint.getTargetRepo());

            // Change origin of the BLUEPRINT REPO correctly templated to the targetRepo
            gitRepo = gitService.changeOrigin(
                    gitRepo,
                    blueprint.getRepositoryBaseUrl() + blueprint.getTargetRepo()
            );

            // Commit and Push the project created from the BLUEPRINT
            gitService.commitAndPushRepo(
                    gitRepo,
                    "Project initialization from blueprint ["
                            + blueprint.getRepositoryBaseUrl()
                            + blueprint.getBlueprintRepo() + "]"
            );
            logger.info("Project correctly pushed");

            // Delete local repository
            gitService.deleteLocalRepository();

        } catch (Throwable t) {
            gitService.deleteLocalRepository();
            throw t;
        }
    }

}
