package org.opendatamesh.platform.pp.blueprint.server.services;

import org.eclipse.jgit.api.Git;
import org.opendatamesh.dpds.location.GitService;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintSearchOptions;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.opendatamesh.platform.pp.blueprint.server.database.repositories.BlueprintRepository;
import org.opendatamesh.platform.pp.blueprint.server.resources.internals.GitCheckResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final Logger logger = LoggerFactory.getLogger(BlueprintService.class);

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Blueprint createBlueprint(Blueprint blueprint, boolean checkBlueprint) {

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

        if (!blueprints.isEmpty()) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_02_BLUEPRINT_ALREADY_EXISTS,
                    "Blueprint [" + blueprint.getName() + "] of repo ["
                            + blueprint.getRepositoryUrl() + "] already exist"
            );
        }

        String tmpDirectory = generateTemporaryDirectoryName();
        if (checkBlueprint) {
            GitCheckResource gitCheckResource = gitCheckerService.checkGitRepositoryAndLoadParams(
                    blueprint.getRepositoryUrl(),
                    blueprint.getBlueprintDirectory(),
                    tmpDirectory
            );

            if (!gitCheckResource.isBlueprintDirectoryCheck()) {
                throw new UnprocessableEntityException(
                        BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                        "Missing blueprintDirectory [" + blueprint.getBlueprintDirectory() + "] in the given repository"
                );
            }

            if (!gitCheckResource.isParamsDescriptionCheck()) {
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
                    "Blueprint [{}] of version [{}] of repository [{}/{}] successfully registered"
                    , blueprint.getName(), blueprint.getVersion(), blueprint.getRepositoryUrl(), blueprint.getBlueprintDirectory());
            return blueprint;
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while saving blueprint [" + blueprint.getName() + "] "
                            + "of repository [" + blueprint.getRepositoryUrl() + "]",
                    e
            );
        }
    }

    private Blueprint saveBlueprint(Blueprint blueprint) {
        return blueprintRepository.saveAndFlush(blueprint);
    }


    // ======================================================================================
    // READ ALL
    // ======================================================================================
    public List<Blueprint> readBlueprints(BlueprintSearchOptions blueprintSearchOptions) {
        try {
            List<Specification<Blueprint>> specs = new ArrayList<>();
            if (StringUtils.hasText(blueprintSearchOptions.getSearch())) {
                specs.add(BlueprintRepository.Specs.search(blueprintSearchOptions.getSearch()));
            }
            return blueprintRepository.findAll(SpecsUtils.combineWithAnd(specs));
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while loading blueprints",
                    e
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
                    "An error occurred in the backend database while loading activity with id [" + blueprintId + "]",
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
        return blueprintLookUpResult.orElse(null);
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================
    public Blueprint updateBlueprint(Long blueprintId, Blueprint blueprint) {

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

        if (!blueprintRepository.existsById(blueprintId)) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        blueprint.setId(blueprintId);

        try {
            blueprint = saveBlueprint(blueprint);
            logger.info("Blueprint with id [" + blueprint.getId() + "] successfully updated");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while updating blueprint with id [" + blueprint.getId() + "]",
                    t
            );
        }

        return blueprint;
    }


    // ======================================================================================
    // DELETE
    // ======================================================================================
    public void deleteBlueprint(Long blueprintId) {

        if (!blueprintRepository.existsById(blueprintId)) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }

        try {
            blueprintRepository.deleteById(blueprintId);
            logger.info("Blueprint with id [" + blueprintId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while deleting blueprint with id [" + blueprintId + "]",
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
                    "An error occurred in the backend database while searching blueprints",
                    t
            );
        }
    }


    // ======================================================================================
    // INSTANCE
    // ======================================================================================
    public void instanceBlueprint(Long blueprintId, ConfigResource configResource) {

        if (configResource == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_02_CONFIG_IS_EMPTY,
                    "Config object cannot be null when performing INSTANCE of a blueprint");
        }

        if (configResource.getTargetRepo() == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_03_CONFIG_IS_INVALID,
                    "Target Repository of Config object cannot be null when performing INSTANCE of a blueprint");
        }

        if (configResource.getConfig() == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_03_CONFIG_IS_INVALID,
                    "Config sections of Config object cannot be null when performing INSTANCE of a blueprint");
        }

        Blueprint blueprint = loadBlueprint(blueprintId);

        if (blueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprintId + "] doesn't exists");
        }


        // Clone the BLUEPRINT repository
        String sourceRepoTmpDirectory = generateTemporaryDirectoryName();
        logger.info("Cloning repository [{}] ...", blueprint.getRepositoryUrl());

        try (Git sourceGitRepo = gitService.cloneRepo(
                blueprint.getRepositoryUrl(),
                sourceRepoTmpDirectory,
                false, null, null)
        ) {
            logger.info("Repository [{}] correctly cloned", blueprint.getRepositoryUrl());

            // Clean the repository to consider only the template
            String targetRepoTmpDirectory = generateTemporaryDirectoryName();
            logger.info("Initializing new Git target repository ...");

            //TODO refactor !!!!
            // AS IS :
            // createRepo == true
            // |---> the repository where the instantiation of the blueprint will be pushed must be inside the organization.
            // |---> the repoBaseUrl is computed like this (very fragile!!!) :
            //                                              String repositoryUrl = "https://github.com/opendatamesh-initiative/odm-demo";
            //                                              int lastSlashIndex = repositoryUrl.lastIndexOf("/");
            //                                              String repoBaseUrl = repositoryUrl.substring(0, lastSlashIndex + 1);
            //                                              repoBaseUrl --> "https://github.com/opendatamesh-initiative/"
            // |---> the targetRepo should contain only the name of another repository inside the organization.
            // createRepo == false
            // |---> the targetRepo should contain the full url of the repository where the instantiation of the blueprint will be pushed.
            String targetRepo = Boolean.TRUE.equals(configResource.getCreateRepo()) ? blueprint.getRepoBaseUrl() + configResource.getTargetRepo() : configResource.getTargetRepo();
            try (Git targetGitRepo = gitService.initTargetRepository(
                    sourceGitRepo,
                    blueprint.getBlueprintDirectory(),
                    configResource.getCreateRepo(),
                    targetRepoTmpDirectory,
                    targetRepo
            )) {
                logger.info("Target repository initialized");
                // Get the working directory of the repository and call the templatingService to instance the BLUEPRINT
                logger.info("Templating the repository ...");
                File workingDirectory = targetGitRepo.getRepository().getWorkTree();
                templatingService.templating(workingDirectory, configResource);
                logger.info("Repository correctly templated");

                if (Boolean.TRUE.equals(configResource.getCreateRepo())) {
                    logger.info("Creating the target repository  on the remote provider ...");
                    // Create the targetRepo
                    //TODO refactor !!!!
                    // The parameter are Git Provider dependant and should not reside inside the Blueprint Resource
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
                        targetGitRepo,
                        "Project initialization from blueprint [" + blueprint.getRepositoryUrl() + "]"
                );
                logger.info("Repository correctly pushed");

            } finally {
                gitService.deleteLocalRepository(targetRepoTmpDirectory);
            }

        } finally {
            gitService.deleteLocalRepository(sourceRepoTmpDirectory);
        }
    }


    private String generateTemporaryDirectoryName() {
        String uuid = UUID.randomUUID().toString();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return templatesPath + File.separator + timestamp + "_" + uuid;
    }
}
