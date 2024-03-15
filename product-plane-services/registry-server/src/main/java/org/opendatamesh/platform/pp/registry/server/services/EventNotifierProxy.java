package org.opendatamesh.platform.pp.registry.server.services;

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
import org.springframework.stereotype.Service;

@Service
public class EventNotifierProxy {

    @Autowired
    EventNotifierClient eventNotifierClient;

    private final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;


    // ======================================================================================
    // Data Product events
    // ======================================================================================

    public void notifyDataProductCreation(DataProductResource dataProduct) {
        EventResource eventResource = buildDataProductEvent(
                EventType.DATA_PRODUCT_CREATED,
                dataProduct.getId(),
                null,
                dataProduct
        );
        notifyEvent(eventResource, "Impossible to upload Data Product to notificationServices: ");
    }

    public void notifyDataProductUpdate(
            DataProductResource previousDataProduct, DataProductResource currentDataProduct
    ) {
        EventResource eventResource = buildDataProductEvent(
                EventType.DATA_PRODUCT_UPDATED,
                currentDataProduct.getId(),
                previousDataProduct,
                currentDataProduct
        );
        notifyEvent(eventResource, "Impossible to upload Data Product to notificationServices: ");
    }

    public void notifyDataProductDeletion(DataProductResource dataProduct) {
        EventResource eventResource = buildDataProductEvent(
                EventType.DATA_PRODUCT_DELETED,
                dataProduct.getId(),
                dataProduct,
                null
        );
        notifyEvent(eventResource, "Impossible to upload Data Product to notificationServices: ");
    }


    // ======================================================================================
    // Data Product Version events
    // ======================================================================================

    public void notifyDataProductVersionCreation(DataProductVersionDPDS dataProductVersion) {
        EventResource eventResource = buildDataProductVersionEvent(
                EventType.DATA_PRODUCT_VERSION_CREATED,
                dataProductVersion.getInfo().getDataProductId(),
                null,
                dataProductVersion
        );
        notifyEvent(eventResource, "Impossible to upload Data Product Version to notificationServices: ");
    }

    public void notifyDataProductVersionDeletion(DataProductVersionDPDS dataProductVersion) {
        EventResource eventResource = buildDataProductVersionEvent(
                EventType.DATA_PRODUCT_DELETED,
                dataProductVersion.getInfo().getDataProductId(),
                dataProductVersion,
                null
        );
        notifyEvent(eventResource, "Impossible to upload Data Product Version to notificationServices: ");
    }


    // ======================================================================================
    // Dispatch events
    // ======================================================================================

    public void notifyEvent(EventResource eventResource, String errorMessage) {
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
