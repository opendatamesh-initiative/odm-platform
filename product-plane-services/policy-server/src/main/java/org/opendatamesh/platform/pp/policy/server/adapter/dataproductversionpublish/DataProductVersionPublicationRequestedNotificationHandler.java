package org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.dpds.exceptions.ParseException;
import org.opendatamesh.dpds.location.DescriptorLocation;
import org.opendatamesh.dpds.location.UriLocation;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.parser.DPDSParser;
import org.opendatamesh.dpds.parser.IdentifierStrategyFactory;
import org.opendatamesh.dpds.parser.ParseOptions;
import org.opendatamesh.dpds.parser.ParseResult;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;
import org.opendatamesh.platform.pp.policy.server.adapter.PolicyV2AdapterNotificationHandler;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.NotificationServiceV2Client;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.emitted.DataProductVersionPublicationApprovedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.emitted.DataProductVersionPublicationRejectedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.received.DataProductVersionPublicationRequestedEventRes;
import org.opendatamesh.platform.pp.policy.server.services.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataProductVersionPublicationRequestedNotificationHandler implements PolicyV2AdapterNotificationHandler {
    private static final NotificationV2EventType SUPPORTED_EVENT = NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED;

    private static final Logger log = LoggerFactory.getLogger(DataProductVersionPublicationRequestedNotificationHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private ValidationService validationService;

    @Autowired
    private NotificationServiceV2Client notificationServiceV2Client;

    @Override
    public boolean supports(NotificationV2Res notification) {
        return SUPPORTED_EVENT.getValue().equals(notification.getEvent().getType());
    }

    @Override
    public void handle(NotificationV2Res notification) {
        try {
            DataProductVersionPublicationRequestedEventRes dataProductVersionPublishEvent = objectMapper.convertValue(notification.getEvent(), DataProductVersionPublicationRequestedEventRes.class);

            String dataProductId = extractDataProductId(dataProductVersionPublishEvent);
            PolicyEvaluationRequestResource evaluationRequest = buildPolicyEvaluationRequestResource(dataProductVersionPublishEvent, dataProductId);
            ValidationResponseResource validationResponse = validationService.validateInput(evaluationRequest, true);

            Object responseEvent = validationResponseToEvent(validationResponse, dataProductVersionPublishEvent, notification.getSequenceId());
            notificationServiceV2Client.notifyEvent(responseEvent);

            // Update notification status to Processed
            notificationServiceV2Client.processingSuccess(notification.getSequenceId());
            log.info("Processed {} event for dataProductId: {}, result: {}",
                    SUPPORTED_EVENT.getValue(), dataProductId, validationResponse.getResult());
        } catch (Exception e) {
            log.error("Error processing {} notification", SUPPORTED_EVENT.getValue(), e);
            notificationServiceV2Client.processingFailure(notification.getSequenceId());
        }
    }

    private Object validationResponseToEvent(ValidationResponseResource validationResponse,
                                             DataProductVersionPublicationRequestedEventRes originalEvent,
                                             Long sequenceId) {
        // Build typed event object based on validation result
        DataProductVersionPublicationRequestedEventRes.DataProductVersionRes sourceDataProductVersion =
                originalEvent.getEventContent().getDataProductVersion();

        // Check if any blocking policies failed
        boolean hasBlockingPolicyFailure = hasBlockingPolicyFailure(validationResponse);

        if (!hasBlockingPolicyFailure) {
            return objectMapper.valueToTree(buildApproveEvent(sequenceId, sourceDataProductVersion));
        } else {
            return objectMapper.valueToTree(buildRejectEvent(sequenceId, sourceDataProductVersion));
        }
    }

    private DataProductVersionPublicationRejectedEventRes buildRejectEvent(Long sequenceId, DataProductVersionPublicationRequestedEventRes.DataProductVersionRes sourceDataProductVersion) {
        DataProductVersionPublicationRejectedEventRes rejectedEvent = new DataProductVersionPublicationRejectedEventRes();
        rejectedEvent.setSequenceId(sequenceId);
        rejectedEvent.setResourceIdentifier(sourceDataProductVersion.getUuid());

        DataProductVersionPublicationRejectedEventRes.EventContent eventContent =
                new DataProductVersionPublicationRejectedEventRes.EventContent();
        eventContent.setDataProductVersion(copyToRejectedDataProductVersion(sourceDataProductVersion));
        rejectedEvent.setEventContent(eventContent);

        return rejectedEvent;
    }

    private DataProductVersionPublicationApprovedEventRes buildApproveEvent(Long sequenceId, DataProductVersionPublicationRequestedEventRes.DataProductVersionRes sourceDataProductVersion) {
        DataProductVersionPublicationApprovedEventRes approvedEvent = new DataProductVersionPublicationApprovedEventRes();
        approvedEvent.setSequenceId(sequenceId);
        approvedEvent.setResourceIdentifier(sourceDataProductVersion.getUuid());

        DataProductVersionPublicationApprovedEventRes.EventContent eventContent =
                new DataProductVersionPublicationApprovedEventRes.EventContent();
        eventContent.setDataProductVersion(copyToApprovedDataProductVersion(sourceDataProductVersion));
        approvedEvent.setEventContent(eventContent);

        return approvedEvent;
    }

    private boolean hasBlockingPolicyFailure(ValidationResponseResource validationResponse) {
        boolean hasBlockingPolicyFailure = false;
        if (validationResponse.getPolicyResults() != null) {
            for (org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource policyResult : validationResponse.getPolicyResults()) {
                if (Boolean.FALSE.equals(policyResult.getResult()) &&
                        policyResult.getPolicy() != null &&
                        Boolean.TRUE.equals(policyResult.getPolicy().getBlockingFlag())) {
                    hasBlockingPolicyFailure = true;
                    break;
                }
            }
        }
        return hasBlockingPolicyFailure;
    }

    /*
     *  This function mimics how the Registry V1 builds the event of DATA_PRODUCT_VERSION_CREATION
     *  To do this, it uses the old Data Product Descriptor parser which has some side effects during parsing,
     *  but they must be maintained to avoid breaking OPA policies built on top of its output
     * */
    private PolicyEvaluationRequestResource buildPolicyEvaluationRequestResource(DataProductVersionPublicationRequestedEventRes event, String dataProductId) {
        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_VERSION_CREATION);
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        evaluationRequest.setDataProductId(dataProductId);

        evaluationRequest.setDataProductVersion(event.getEventContent().getDataProductVersion().getTag());

        JsonNode newDescriptorJson = event.getEventContent().getDataProductVersion().getContent();
        JsonNode oldDescriptorJson = Optional.ofNullable(event.getEventContent().getPreviousDataProductVersion())
                .map(DataProductVersionPublicationRequestedEventRes.DataProductVersionRes::getContent)
                .orElse(null);

        DataProductVersionDPDS newDpds = parseDataProductVersionDPDS(newDescriptorJson);
        DataProductVersionDPDS oldDpds = oldDescriptorJson != null ? parseDataProductVersionDPDS(oldDescriptorJson) : null;

        JsonNode oldState = oldDpds != null ? objectMapper.valueToTree(new RegistryV1DataProductVersionEventState(oldDpds)) : null;
        JsonNode newState = objectMapper.valueToTree(new RegistryV1DataProductVersionEventState(newDpds));

        if (oldState != null) {
            fixDpdsVersionFieldName(oldState);
        }
        fixDpdsVersionFieldName(newState);

        evaluationRequest.setCurrentState(oldState);
        evaluationRequest.setAfterState(newState);

        return evaluationRequest;
    }

    private void fixDpdsVersionFieldName(JsonNode eventStateTree) {
        if (eventStateTree != null && eventStateTree.has("dataProductVersion")
                && eventStateTree.get("dataProductVersion").has("info")
                && eventStateTree.get("dataProductVersion").get("info").has("versionNumber")) {
            JsonNode versionNumberNode = eventStateTree.get("dataProductVersion").get("info").get("versionNumber");
            ((ObjectNode) eventStateTree.get("dataProductVersion").get("info")).remove("versionNumber");
            ((ObjectNode) eventStateTree.get("dataProductVersion").get("info")).set("version", versionNumberNode);
        }
    }

    private DataProductVersionDPDS parseDataProductVersionDPDS(JsonNode descriptorJson) {
        try {
            // Convert JsonNode to String
            String descriptorContent = objectMapper.writeValueAsString(descriptorJson);

            // Create DPDSParser
            DPDSParser descriptorParser = new DPDSParser(
                    "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/",
                    "1.0.0",
                    "1.0.0"
            );

            // Create DescriptorLocation from content
            DescriptorLocation location = new UriLocation(descriptorContent);

            // Configure parse options
            ParseOptions options = new ParseOptions();
            options.setIdentifierStrategy(IdentifierStrategyFactory.getDefault());

            // Parse descriptor
            ParseResult result = descriptorParser.parse(location, options);
            return result.getDescriptorDocument();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert descriptor JSON to string", e);
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse data product version descriptor", e);
        }
    }

    private String extractDataProductId(DataProductVersionPublicationRequestedEventRes event) {
        String dataProductId = event.getResourceIdentifier();
        if (dataProductId == null || dataProductId.isEmpty()) {
            throw new IllegalStateException("Missing resourceIdentifier in notification event");
        }
        return dataProductId;
    }

    private DataProductVersionPublicationApprovedEventRes.DataProductVersionRes copyToApprovedDataProductVersion(
            DataProductVersionPublicationRequestedEventRes.DataProductVersionRes source) {
        DataProductVersionPublicationApprovedEventRes.DataProductVersionRes target =
                new DataProductVersionPublicationApprovedEventRes.DataProductVersionRes();
        target.setUuid(source.getUuid());
        target.setTag(source.getTag());

        if (source.getDataProduct() != null) {
            DataProductVersionPublicationApprovedEventRes.DataProductRes targetDataProduct =
                    new DataProductVersionPublicationApprovedEventRes.DataProductRes();
            targetDataProduct.setUuid(source.getDataProduct().getUuid());
            targetDataProduct.setFqn(source.getDataProduct().getFqn());
            target.setDataProduct(targetDataProduct);
        }

        return target;
    }

    private DataProductVersionPublicationRejectedEventRes.DataProductVersionRes copyToRejectedDataProductVersion(
            DataProductVersionPublicationRequestedEventRes.DataProductVersionRes source) {
        DataProductVersionPublicationRejectedEventRes.DataProductVersionRes target =
                new DataProductVersionPublicationRejectedEventRes.DataProductVersionRes();
        target.setUuid(source.getUuid());
        target.setTag(source.getTag());

        if (source.getDataProduct() != null) {
            DataProductVersionPublicationRejectedEventRes.DataProductRes targetDataProduct =
                    new DataProductVersionPublicationRejectedEventRes.DataProductRes();
            targetDataProduct.setUuid(source.getDataProduct().getUuid());
            targetDataProduct.setFqn(source.getDataProduct().getFqn());
            target.setDataProduct(targetDataProduct);
        }

        return target;
    }

    public static class RegistryV1DataProductVersionEventState {
        private DataProductVersionDPDS dataProductVersion;

        public RegistryV1DataProductVersionEventState() {
        }

        public RegistryV1DataProductVersionEventState(DataProductVersionDPDS dataProductVersion) {
            this.dataProductVersion = dataProductVersion;
        }

        public DataProductVersionDPDS getDataProductVersion() {
            return dataProductVersion;
        }

        public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
            this.dataProductVersion = dataProductVersion;
        }
    }
}
