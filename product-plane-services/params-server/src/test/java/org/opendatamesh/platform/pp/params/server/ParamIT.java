package org.opendatamesh.platform.pp.params.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.opendatamesh.platform.pp.params.api.resources.ParamsApiStandardErrors;
import org.opendatamesh.platform.pp.params.server.services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParamIT extends ODMParamsIT {

    @Autowired
    EncryptionService encryptionService;

    // ======================================================================================
    // CREATE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateParam() throws Exception {

        // Not Secret
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);

        // Secret
        paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_2);
        verifyResourceParamTwo(paramResource);

    }


    // ======================================================================================
    // UPDATE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateParam() throws Exception {

        // Not Secret
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);
        paramResource.setParamValue("8002");
        ResponseEntity<ParamResource> putResponse = paramsClient.updateParam(paramResource.getId(), paramResource);
        verifyResourceParamOneUpdated(putResponse.getBody());

        // Secret
        paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_2);
        verifyResourceParamTwo(paramResource);
        paramResource.setParamValue("pwd321");
        putResponse = paramsClient.updateParam(paramResource.getId(), paramResource);
        verifyResourceParamTwoUpdated(putResponse.getBody());

    }


    // ======================================================================================
    // READ Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllParam() throws Exception {

        // Resources
        // Not Secret
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);
        // Secret
        paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_2);
        verifyResourceParamTwo(paramResource);

        // Client without decryption ability
        ResponseEntity<ParamResource[]> getResponse = paramsClient.getParams();
        List<ParamResource> params = List.of(getResponse.getBody());
        assertThat(params.size()).isEqualTo(2);
        verifyResourceParamOne(params.get(0));
        verifyResourceParamTwo(params.get(1));

        // Client with decryption ability
        getResponse = paramsClientWithDecryption.getParams();
        params = List.of(getResponse.getBody());
        assertThat(params.size()).isEqualTo(2);
        verifyResourceParamOne(params.get(0));
        verifyResourceParamTwoDecrypted(params.get(1));

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneParam() throws Exception {

        // Resources
        // Not Secret
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);
        Long paramOneId = paramResource.getId();
        // Secret
        paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_2);
        verifyResourceParamTwo(paramResource);
        Long paramTwoId = paramResource.getId();

        // Client with decryption ability
        // Not Secret
        ResponseEntity<ParamResource> getResponse = paramsClientWithDecryption.getOneParam(paramOneId);
        paramResource = getResponse.getBody();
        verifyResourceParamOne(paramResource);
        // Secret
        getResponse = paramsClientWithDecryption.getOneParam(paramTwoId);
        paramResource = getResponse.getBody();
        verifyResourceParamTwoDecrypted(paramResource);

        // Client without decryption ability
        // Not Secret - the same of the previous client - ignored
        // Secret
        getResponse = paramsClient.getOneParam(paramTwoId);
        paramResource = getResponse.getBody();
        verifyResourceParamTwo(paramResource);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneParamByName() throws Exception {

        // Resources
        // Not Secret
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);
        String paramOneName = paramResource.getParamName();
        // Secret
        paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_2);
        verifyResourceParamTwo(paramResource);
        String paramTwoName = paramResource.getParamName();

        // Client with decryption ability
        // Not Secret
        ResponseEntity<ParamResource> getResponse = paramsClientWithDecryption.getOneParamByName(paramOneName);
        paramResource = getResponse.getBody();
        verifyResourceParamOne(paramResource);
        // Secret
        getResponse = paramsClientWithDecryption.getOneParamByName(paramTwoName);
        paramResource = getResponse.getBody();
        verifyResourceParamTwoDecrypted(paramResource);

        // Client without decryption ability
        // Not Secret - the same of the previous client - ignored
        // Secret
        getResponse = paramsClient.getOneParamByName(paramTwoName);
        paramResource = getResponse.getBody();
        verifyResourceParamTwo(paramResource);

    }


    // ======================================================================================
    // DELETE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteParam() throws Exception {

        // Resources
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        verifyResourceParamOne(paramResource);

        // Deletion
        ResponseEntity<Void> deleteResponse = paramsClient.deleteParam(paramResource.getId());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Check record does not exist anymore on the DB
        ResponseEntity<ErrorRes> readAfterDeleteResponse = paramsClient.getOneParam(paramResource.getId());
        verifyResponseError(
                readAfterDeleteResponse,
                HttpStatus.NOT_FOUND,
                ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourceParamOne(ParamResource paramResource) {
        assertThat(paramResource.getId()).isNotNull();
        assertThat(paramResource.getParamName()).isEqualTo("spring.port");
        assertThat(paramResource.getParamValue()).isEqualTo("8001");
        assertThat(paramResource.getDisplayName()).isEqualTo("Registry Spring Port");
        assertThat(paramResource.getDescription()).isEqualTo("Port of the Registry Server application");
        assertThat(paramResource.getSecret()).isEqualTo(false);
        assertThat(paramResource.getCreatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isNull();
    }

    private void verifyResourceParamOneUpdated(ParamResource paramResource) {
        assertThat(paramResource.getId()).isNotNull();
        assertThat(paramResource.getParamName()).isEqualTo("spring.port");
        assertThat(paramResource.getParamValue()).isEqualTo("8002");
        assertThat(paramResource.getDisplayName()).isEqualTo("Registry Spring Port");
        assertThat(paramResource.getDescription()).isEqualTo("Port of the Registry Server application");
        assertThat(paramResource.getSecret()).isEqualTo(false);
        assertThat(paramResource.getCreatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isAfter(paramResource.getCreatedAt());
    }

    private void verifyResourceParamTwo(ParamResource paramResource) throws Exception {
        assertThat(paramResource.getId()).isNotNull();
        assertThat(paramResource.getParamName()).isEqualTo("spring.datasource.password");
        assertThat(paramResource.getDisplayName()).isEqualTo("DB password");
        assertThat(paramResource.getDescription()).isEqualTo("Password of the database");
        assertThat(paramResource.getSecret()).isEqualTo(true);
        assertThat(paramResource.getParamValue()).isNotEqualTo("pwd123");
        assertThat(encryptionService.decrypt(paramResource.getParamValue())).isEqualTo("pwd123");
        assertThat(paramResource.getCreatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isNull();
    }

    private void verifyResourceParamTwoDecrypted(ParamResource paramResource) throws Exception {
        assertThat(paramResource.getId()).isNotNull();
        assertThat(paramResource.getParamName()).isEqualTo("spring.datasource.password");
        assertThat(paramResource.getDisplayName()).isEqualTo("DB password");
        assertThat(paramResource.getDescription()).isEqualTo("Password of the database");
        assertThat(paramResource.getSecret()).isEqualTo(true);
        assertThat(paramResource.getParamValue()).isEqualTo("pwd123");
        assertThat(paramResource.getCreatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isNull();
    }

    private void verifyResourceParamTwoUpdated(ParamResource paramResource) throws Exception {
        assertThat(paramResource.getId()).isNotNull();
        assertThat(paramResource.getParamName()).isEqualTo("spring.datasource.password");
        assertThat(paramResource.getDisplayName()).isEqualTo("DB password");
        assertThat(paramResource.getDescription()).isEqualTo("Password of the database");
        assertThat(paramResource.getSecret()).isEqualTo(true);
        assertThat(paramResource.getParamValue()).isNotEqualTo("pwd321");
        assertThat(encryptionService.decrypt(paramResource.getParamValue())).isEqualTo("pwd321");
        assertThat(paramResource.getCreatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isNotNull();
        assertThat(paramResource.getUpdatedAt()).isAfter(paramResource.getCreatedAt());
    }

}
