package org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;
import org.opendatamesh.platform.pp.policy.server.adapter.PolicyV2AdapterNotificationHandler;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.NotificationServiceV2Client;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.emitted.DataProductInitializationApprovedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.emitted.DataProductInitializationRejectedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.received.DataProductInitializationRequestedEventRes;
import org.opendatamesh.platform.pp.policy.server.services.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataProductInitializationRequestedNotificationHandler implements PolicyV2AdapterNotificationHandler {
    private static final NotificationV2EventType SUPPORTED_EVENT = NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REQUESTED;

    private static final Logger log = LoggerFactory.getLogger(DataProductInitializationRequestedNotificationHandler.class);

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
            DataProductInitializationRequestedEventRes dataProductInitEvent = objectMapper.convertValue(notification.getEvent(), DataProductInitializationRequestedEventRes.class);

            String dataProductId = extractDataProductId(dataProductInitEvent);
            PolicyEvaluationRequestResource evaluationRequest = buildPolicyEvaluationRequestResource(dataProductInitEvent, dataProductId);
            ValidationResponseResource validationResponse = validationService.validateInput(evaluationRequest, true);

            Object responseEvent = validationResponseToEvent(validationResponse, dataProductInitEvent);
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

    private Object validationResponseToEvent(ValidationResponseResource validationResponse, DataProductInitializationRequestedEventRes originalEvent) {
        // Check if any blocking policies failed
        boolean hasBlockingPolicyFailure = hasBlockingPolicyFailure(validationResponse);

        if (!hasBlockingPolicyFailure) {
            return objectMapper.valueToTree(buildApproveEvent(originalEvent));
        } else {
            return objectMapper.valueToTree(buildRejectEvent(originalEvent));
        }
    }

    private DataProductInitializationRejectedEventRes buildRejectEvent(DataProductInitializationRequestedEventRes originalEvent) {
        DataProductInitializationRejectedEventRes rejectedEvent = new DataProductInitializationRejectedEventRes();
        rejectedEvent.setResourceIdentifier(originalEvent.getResourceIdentifier());

        DataProductInitializationRejectedEventRes.EventContent eventContent =
                new DataProductInitializationRejectedEventRes.EventContent();
        DataProductInitializationRejectedEventRes.DataProductRes dataProduct =
                new DataProductInitializationRejectedEventRes.DataProductRes();
        dataProduct.setUuid(originalEvent.getEventContent().getDataProduct().getUuid());
        dataProduct.setFqn(originalEvent.getEventContent().getDataProduct().getFqn());
        eventContent.setDataProduct(dataProduct);
        rejectedEvent.setEventContent(eventContent);

        return rejectedEvent;
    }

    private DataProductInitializationApprovedEventRes buildApproveEvent(DataProductInitializationRequestedEventRes originalEvent) {
        DataProductInitializationApprovedEventRes approvedEvent = new DataProductInitializationApprovedEventRes();
        approvedEvent.setResourceIdentifier(originalEvent.getResourceIdentifier());

        DataProductInitializationApprovedEventRes.EventContent eventContent =
                new DataProductInitializationApprovedEventRes.EventContent();
        DataProductInitializationApprovedEventRes.DataProductRes dataProduct =
                new DataProductInitializationApprovedEventRes.DataProductRes();
        dataProduct.setUuid(originalEvent.getEventContent().getDataProduct().getUuid());
        dataProduct.setFqn(originalEvent.getEventContent().getDataProduct().getFqn());
        eventContent.setDataProduct(dataProduct);
        approvedEvent.setEventContent(eventContent);

        return approvedEvent;
    }

    private boolean hasBlockingPolicyFailure(ValidationResponseResource validationResponse) {
        boolean hasBlockingPolicyFailure = false;
        if (validationResponse.getPolicyResults() != null) {
            for (PolicyEvaluationResultResource policyResult : validationResponse.getPolicyResults()) {
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

    private PolicyEvaluationRequestResource buildPolicyEvaluationRequestResource(DataProductInitializationRequestedEventRes event, String dataProductId) {
        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        evaluationRequest.setDataProductId(dataProductId);

        RegistryV1DataProductResource registryV1DataProductResource = mapDataProductV2toV1(event.getEventContent().getDataProduct());

        RegistryV1DataProductEventState eventState = new RegistryV1DataProductEventState();
        eventState.setDataProduct(registryV1DataProductResource);

        evaluationRequest.setAfterState(objectMapper.valueToTree(eventState));
        // Set currentState to null (as per test scenario - missing currentState is handled gracefully)
        evaluationRequest.setCurrentState(null);
        return evaluationRequest;
    }

    private RegistryV1DataProductResource mapDataProductV2toV1(DataProductInitializationRequestedEventRes.DataProductRes dataProductV2) {
        RegistryV1DataProductResource registryV1DataProductResource = new RegistryV1DataProductResource();
        registryV1DataProductResource.setId(dataProductV2.getUuid());
        registryV1DataProductResource.setDescription(dataProductV2.getDescription());
        registryV1DataProductResource.setDomain(dataProductV2.getDomain());
        registryV1DataProductResource.setFullyQualifiedName(dataProductV2.getFqn());
        return registryV1DataProductResource;
    }

    private String extractDataProductId(DataProductInitializationRequestedEventRes dataProductInitEvent) {
        String dataProductId = dataProductInitEvent.getResourceIdentifier();
        if (dataProductId == null || dataProductId.isEmpty()) {
            throw new IllegalStateException("Missing resourceIdentifier in notification event");
        }
        return dataProductId;
    }

    public class RegistryV1DataProductEventState {

        private RegistryV1DataProductResource dataProduct;

        public RegistryV1DataProductEventState() {
        }

        public RegistryV1DataProductEventState(RegistryV1DataProductResource dataProduct) {
            this.dataProduct = dataProduct;
        }

        public RegistryV1DataProductResource getDataProduct() {
            return dataProduct;
        }

        public void setDataProduct(RegistryV1DataProductResource dataProduct) {
            this.dataProduct = dataProduct;
        }
    }

    public class RegistryV1DataProductResource {
        private String id;
        private String fullyQualifiedName;
        private String description;
        private String domain;

        public RegistryV1DataProductResource() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFullyQualifiedName() {
            return fullyQualifiedName;
        }

        public void setFullyQualifiedName(String fullyQualifiedName) {
            this.fullyQualifiedName = fullyQualifiedName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }

}
