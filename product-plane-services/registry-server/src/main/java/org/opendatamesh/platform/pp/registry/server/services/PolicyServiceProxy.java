package org.opendatamesh.platform.pp.registry.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(
            @Value("${odm.productPlane.policyService.address}") final String serverAddress,
            @Value("${odm.productPlane.policyService.active}") String policyServiceActive
    ) {
        if ("true".equals(policyServiceActive)) {
            this.policyClient = new PolicyClientImpl(
                    serverAddress,
                    ObjectMapperFactory.JSON_MAPPER
            );
        } else {
            //TODO
        }
    }

    // TODO return also why is not compliant
    public Boolean validateDataProductVersionCreation(DataProductVersionDPDS dataProductVersion) throws JsonProcessingException {

        // EVENT creation
        // TODO: check if is an UPDATE (new version of an existing product) or a NEW ENTITY to set beforeState and afterState
        EventResource eventResource = new EventResource(
                EventType.DATA_PRODUCT_VERSION_CREATED,
                dataProductVersion.getInfo().getDataProductId(),
                null, // BEFORE STATE
                dataProductVersion.toEventString() // AFTER STATE
        );

        PolicyEvaluationResultResource result = policyClient.validateObject(eventResource);
        return result.getResult();
    }

}
