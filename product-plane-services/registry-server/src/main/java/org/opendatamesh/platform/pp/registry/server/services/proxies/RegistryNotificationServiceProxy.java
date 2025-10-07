package org.opendatamesh.platform.pp.registry.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.clients.DispatchClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.events.DataProductEventState;
import org.opendatamesh.platform.pp.registry.api.resources.events.DataProductVersionEventState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistryNotificationServiceProxy {

    @Autowired(required = false)
    DispatchClient notificationClient;

    @Value("${odm.productPlane.notificationService.active}")
    private Boolean notificationServiceActive;

    private final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;


    // ======================================================================================
    // Data Product events
    // ======================================================================================

    public void notifyDataProductCreation(DataProductResource dataProduct) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_CREATED,
                    dataProduct.getId(),
                    null,
                    dataProduct
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyDataProductUpdate(
            DataProductResource previousDataProduct, DataProductResource currentDataProduct
    ) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_UPDATED,
                    currentDataProduct.getId(),
                    previousDataProduct,
                    currentDataProduct
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyDataProductDeletion(DataProductResource dataProduct) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            EventResource eventResource = buildDataProductEvent(
                    EventType.DATA_PRODUCT_DELETED,
                    dataProduct.getId(),
                    dataProduct,
                    null
            );
            notifyEvent(eventResource);
        }
    }


    // ======================================================================================
    // Data Product Version events
    // ======================================================================================

    public void notifyDataProductVersionCreation(DataProductVersionDPDS dataProductVersion) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            EventResource eventResource = buildDataProductVersionEvent(
                    EventType.DATA_PRODUCT_VERSION_CREATED,
                    dataProductVersion.getInfo().getDataProductId(),
                    null,
                    dataProductVersion
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyDataProductVersionDeletion(DataProductVersionDPDS dataProductVersion) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            EventResource eventResource = buildDataProductVersionEvent(
                    EventType.DATA_PRODUCT_VERSION_DELETED,
                    dataProductVersion.getInfo().getDataProductId(),
                    dataProductVersion,
                    null
            );
            notifyEvent(eventResource);
        }
    }


    private void notifyEvent(EventResource eventResource) {
        try {
            notificationClient.notifyEvent(eventResource);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }


    // ======================================================================================
    // Event creation
    // ======================================================================================

    private EventResource buildDataProductEvent(
            EventType eventType,
            String dataProductId,
            DataProductResource oldDataProduct,
            DataProductResource newDataProduct
    ) {
        return new EventResource(
                eventType,
                dataProductId,
                mapper.valueToTree(new DataProductEventState(oldDataProduct)),
                mapper.valueToTree(new DataProductEventState(newDataProduct))
        );
    }

    private EventResource buildDataProductVersionEvent(
            EventType eventType, String dataProductVersionId, DataProductVersionDPDS oldDpds, DataProductVersionDPDS newDpds
    ) {
        JsonNode oldState = mapper.valueToTree(new DataProductVersionEventState(oldDpds));
        JsonNode newState = mapper.valueToTree(new DataProductVersionEventState(newDpds));
        fixDpdsVersionFieldName(oldState);
        fixDpdsVersionFieldName(newState);

        return new EventResource(
                eventType,
                dataProductVersionId,
                oldState,
                newState
        );
    }

    private void fixDpdsVersionFieldName(JsonNode eventStateTree) {
        //TODO this must be fixed on the dpds model!!!!
        if (eventStateTree != null && eventStateTree.has("dataProductVersion") && eventStateTree.get("dataProductVersion").has("info") && eventStateTree.get("dataProductVersion").get("info").has("versionNumber")) {
            JsonNode versionNumberNode = eventStateTree.get("").get("info").get("versionNumber");
            ((ObjectNode) versionNumberNode.get("dataProductVersion").get("info")).remove("versionNumber");
            ((ObjectNode) versionNumberNode.get("dataProductVersion").get("info")).set("version", versionNumberNode);
        }
    }

}
