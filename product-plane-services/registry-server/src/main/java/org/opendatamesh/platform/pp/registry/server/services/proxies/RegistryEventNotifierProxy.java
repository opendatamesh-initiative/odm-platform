package org.opendatamesh.platform.pp.registry.server.services.proxies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClient;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistryEventNotifierProxy {

    @Autowired(required = false)
    EventNotifierClient eventNotifierClient;

    @Value("${odm.productPlane.eventNotifierService.active}")
    private Boolean eventNotifierServiceActive;

    private final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;


    // ======================================================================================
    // Data Product events
    // ======================================================================================

    public void notifyDataProductCreation(DataProductResource dataProduct) {
        if(eventNotifierServiceActive) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_CREATED,
                    dataProduct.getId(),
                    null,
                    dataProduct
            );
            notifyDataProductEvent(eventResource);
        }
    }

    public void notifyDataProductUpdate(
            DataProductResource previousDataProduct, DataProductResource currentDataProduct
    ) {
        if(eventNotifierServiceActive) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_UPDATED,
                    currentDataProduct.getId(),
                    previousDataProduct,
                    currentDataProduct
            );
            notifyDataProductEvent(eventResource);
        }
    }

    public void notifyDataProductDeletion(DataProductResource dataProduct) {
        if(eventNotifierServiceActive) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_DELETED,
                    dataProduct.getId(),
                    dataProduct,
                    null
            );
            notifyDataProductEvent(eventResource);
        }
    }


    // ======================================================================================
    // Data Product Version events
    // ======================================================================================

    public void notifyDataProductVersionCreation(DataProductVersionDPDS dataProductVersion) {
        if(eventNotifierServiceActive) {
            EventResource eventResource = buildDataProductVersionEvent(
                    EventType.DATA_PRODUCT_VERSION_CREATED,
                    dataProductVersion.getInfo().getDataProductId(),
                    null,
                    dataProductVersion
            );
            notifyDataProductVersionEvent(eventResource);
        }
    }

    public void notifyDataProductVersionDeletion(DataProductVersionDPDS dataProductVersion) {
        if(eventNotifierServiceActive) {
            EventResource eventResource = buildDataProductVersionEvent(
                    EventType.DATA_PRODUCT_DELETED,
                    dataProductVersion.getInfo().getDataProductId(),
                    dataProductVersion,
                    null
            );
            notifyDataProductVersionEvent(eventResource);
        }
    }


    // ======================================================================================
    // Dispatch events
    // ======================================================================================

    private void notifyDataProductEvent(EventResource eventResource) {
        notifyEvent(
                eventResource,
                "Impossible to upload Data Product to notificationServices: "
        );
    }

    private void notifyDataProductVersionEvent(EventResource eventResource) {
        notifyEvent(
                eventResource,
                "Impossible to upload Data Product Version to notificationServices: "
        );
    }

    private void notifyEvent(EventResource eventResource, String errorMessage) {
        try {
            eventNotifierClient.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    errorMessage + t.getMessage(),
                    t
            );
        }
    }


    // ======================================================================================
    // Event creation
    // ======================================================================================

    private EventResource buildDataProductEvent(
            EventType eventType, String eventSubjectId, Object beforeState, Object afterState
    ) {
        try {
            return new EventResource(
                    eventType,
                    eventSubjectId,
                    beforeState == null ? null : mapper.writeValueAsString(beforeState),
                    afterState == null ? null : mapper.writeValueAsString(afterState)
            );
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing Data Product as JSON: " + e.getMessage()
            );
        }
    }

    private EventResource buildDataProductVersionEvent(
            EventType eventType, String eventSubjectId, Object beforeState, Object afterState
    ) {
        try {
            return new EventResource(
                    eventType,
                    eventSubjectId,
                    beforeState == null ? null : mapper.writeValueAsString(beforeState).replace("versionNumber", "version"),
                    afterState == null ? null : mapper.writeValueAsString(afterState).replace("versionNumber", "version")
            );
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing Data Product Version as JSON: " + e.getMessage()
            );
        }
    }


}
