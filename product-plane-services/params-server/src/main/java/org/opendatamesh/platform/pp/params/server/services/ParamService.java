package org.opendatamesh.platform.pp.params.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.params.api.resources.ParamsApiStandardErrors;
import org.opendatamesh.platform.pp.params.server.database.entities.Param;
import org.opendatamesh.platform.pp.params.server.database.repositories.ParamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParamService {

    @Autowired
    ParamRepository paramRepository;

    @Autowired
    EncryptionService encryptionService;

    @Value("${encryption.enabled}")
    private Boolean encryptionEnabled;

    @Value("${encryption.acceptedClientUUID}")
    private String encryptionAcceptedClientUUID;

    private static final Logger logger = LoggerFactory.getLogger(ParamService.class);


    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Param createParam(Param param) {

        if (param == null) {
            throw new BadRequestException(
                    ParamsApiStandardErrors.SC400_01_PARAM_IS_EMPTY,
                    "Param object cannot be null");
        }

        if (param.getParamName() == null) {
            throw new UnprocessableEntityException(
                    ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                    "Param name cannot be null"
            );
        }

        if (param.getParamValue() == null) {
            throw new UnprocessableEntityException(
                    ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                    "Param value cannot be null"
            );
        }

        Param oldParam = loadParam(param.getParamName());

        if (oldParam != null) {
            throw new UnprocessableEntityException(
                    ParamsApiStandardErrors.SC422_02_PARAMETER_ALREADY_EXISTS,
                    "Parameter [" + param.getParamName() + "] already exist"
            );
        }

        if (encryptionEnabled && param.getSecret() != null && param.getSecret())
            param.setParamValue(encryptParam(param.getParamName(), param.getParamValue()));

        try {
            param = saveParam(param);
            logger.info("Param [" + param.getParamName() + "] succesfully registered");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving param [" + param.getParamName() + "]",
                    t
            );
        }

        return param;
    }

    private Param saveParam(Param param) {
        return paramRepository.saveAndFlush(param);
    }


    // ======================================================================================
    // READ ALL
    // ======================================================================================

    public List<Param> readParams(String clientUUID) {
        try {
            List<Param> params = paramRepository.findAll();
            if(encryptionEnabled && clientUUID != null && encryptionAcceptedClientUUID.equals(clientUUID)) {
                for (Param param : params) {
                    if(param.getSecret())
                        param.setParamValue(decryptParam(param.getParamName(), param.getParamValue()));
                }
            }
            return params;
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading parameters",
                    t
            );
        }
    }
    
    
    // ======================================================================================
    // READ ONE
    // ======================================================================================

    public Param readOneParam(Long paramId, String clientUUID) {

        Param param = null;

        try {
            param = loadParam(paramId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading parameter with id [" + paramId + "]",
                    t
            );
        }

        if (param == null) {
            throw new NotFoundException(
                    ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                    "Param with id [" + paramId + "] does not exist"
            );
        }

        if (encryptionEnabled && clientUUID != null && encryptionAcceptedClientUUID.equals(clientUUID) && param.getSecret())
            param.setParamValue(decryptParam(param.getParamName(), param.getParamValue()));

        return param;

    }

    public Param readOneParamByName(String paramName, String clientUUID) {

        Param param = null;

        try {
            param = loadParam(paramName);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading parameter with name [" + paramName + "]",
                    t
            );
        }

        if (param == null) {
            throw new NotFoundException(
                    ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                    "Param with name [" + paramName + "] does not exist"
            );
        }

        if (encryptionEnabled && clientUUID != null && encryptionAcceptedClientUUID.equals(clientUUID) && param.getSecret())
            param.setParamValue(decryptParam(paramName, param.getParamValue()));

        return param;

    }

    private Param loadParam(Long paramId) {
        Optional<Param> paramLookUpResult = paramRepository.findById(paramId);
        if (paramLookUpResult.isPresent())
            return paramLookUpResult.get();
        else
            return null;
    }

    private Param loadParam(String paramName) {
        List<Param> paramLookUpResult = paramRepository.findByParamName(paramName);
        if (paramLookUpResult.size() > 0)
            return paramLookUpResult.get(0);
        else
            return null;
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Param updateParam(Long paramId, Param param) {

        if(param == null) {
            throw new BadRequestException(
                    ParamsApiStandardErrors.SC400_01_PARAM_IS_EMPTY,
                    "Param object cannot be null");
        }

        if (param.getParamName() == null) {
            throw new UnprocessableEntityException(
                    ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                    "Param name cannot be null"
            );
        }

        if (param.getParamValue() == null) {
            throw new UnprocessableEntityException(
                    ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                    "Param value cannot be null"
            );
        }

        if(!paramRepository.existsById(paramId)) {
            throw new NotFoundException(
                    ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                    "Parameter with id [" + paramId + "] doesn't exists");
        }

        param.setId(paramId);

        if (param.getSecret() != null && param.getSecret())
            param.setParamValue(encryptParam(param.getParamName(), param.getParamValue()));

        try {
            param = saveParam(param);
            logger.info("Parameter with id [" + param.getId() + "] succesfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating parameter with id [" + param.getId() + "]",
                    t
            );
        }

        return param;

    }


    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteParam(Long paramId) {

        if(!paramRepository.existsById(paramId)) {
            throw new NotFoundException(
                    ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                    "Parameter with id [" + paramId + "] doesn't exists");
        }

        try {
            paramRepository.deleteById(paramId);
            logger.info("Parameter with id [" + paramId + "] succesfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting parameter with id [" + paramId + "]",
                    t
            );
        }
    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private String encryptParam(String paramName, String paramValue) {
        try {
            return encryptionService.encrypt(paramValue);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ParamsApiStandardErrors.SC500_03_ENCRYPTION_ERROR,
                    "An error occured in the encryption service while decrypting parameter [" + paramName + "]",
                    t
            );
        }
    }

    private String decryptParam(String paramName, String paramValue) {
        try {
            return encryptionService.decrypt(paramValue);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ParamsApiStandardErrors.SC500_03_ENCRYPTION_ERROR,
                    "An error occured in the encryption service while decrypting parameter [" + paramName + "]",
                    t
            );
        }
    }

}
