package org.opendatamesh.platform.pp.blueprint.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.opendatamesh.platform.pp.blueprint.server.database.repositories.BlueprintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlueprintService {

    @Autowired
    BlueprintRepository blueprintRepository;

    private static final Logger logger = LoggerFactory.getLogger(BlueprintService.class);


    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Blueprint createBlueprint(Blueprint blueprint) {

        if (blueprint == null) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC400_02_BLUEPRINT_IS_EMPTY,
                    "Blueprint object cannot be null");
        }

        if (blueprint.getRepositoryUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintPath() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint path inside repository cannot be null"
            );
        }

        List<Blueprint> blueprints = searchBlueprints(
                blueprint.getRepositoryUrl(),
                blueprint.getBlueprintPath()
        );

        if (blueprints != null && blueprints.isEmpty() == false) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_02_BLUEPRINT_ALREADY_EXISTS,
                    "Blueprint [" + blueprint.getName() + "] in repo [" + blueprint.getRepositoryUrl() + "] in path ["
                            + blueprint.getBlueprintPath() + "] already exist"
            );
        }

        try {
            blueprint = saveBlueprint(blueprint);
            logger.info("Blueprint [" + blueprint.getName() + "] "
                    + "of version [" + blueprint.getVersion() + "] succesfully created"
                    + "on repository [" + blueprint.getRepositoryUrl() + "] "
                    + "in path [" + blueprint.getBlueprintPath() + "]");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving activity [" + blueprint.getName() + "] "
                            + "on repository [" + blueprint.getRepositoryUrl() + "] "
                            + "in path [" + blueprint.getBlueprintPath() + "]",
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

        if(blueprintId == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_01_BLUEPRINT_ID_IS_EMPTY,
                    "Blueprint ID is empty"
            );
        }

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

    public Blueprint updateBlueprint(Blueprint blueprint) {

        if(blueprint == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_02_BLUEPRINT_IS_EMPTY,
                    "Blueprint object cannot be null");
        }

        if(blueprint.getId() == null) {
            throw new BadRequestException(
                    BlueprintApiStandardErrors.SC400_01_BLUEPRINT_ID_IS_EMPTY,
                    "Blueprint ID cannot be null");
        }

        if (blueprint.getRepositoryUrl() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint repository URL cannot be null"
            );
        }

        if (blueprint.getBlueprintPath() == null) {
            throw new UnprocessableEntityException(
                    BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                    "Blueprint path inside repository cannot be null"
            );
        }

        Blueprint oldBlueprint = loadBlueprint(blueprint.getId());
        if(oldBlueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprint.getId() + "] doesn't exists");
        }

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

        Blueprint blueprint = loadBlueprint(blueprintId);
        if(blueprint == null) {
            throw new NotFoundException(
                    BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND,
                    "Blueprint with id [" + blueprint.getId() + "] doesn't exists");
        }

        try {
            blueprintRepository.deleteById(blueprintId);
            logger.info("Blueprint with id [" + blueprint.getId() + "] succesfully deleted");
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

    private List<Blueprint> searchBlueprints(String repositoryUrl, String blueprintPath) {
        try {
            return blueprintRepository.findAll(
                    BlueprintRepository.Specs.hasMatch(repositoryUrl, blueprintPath)
            );
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching blueprints",
                    t
            );
        }
    }

}
