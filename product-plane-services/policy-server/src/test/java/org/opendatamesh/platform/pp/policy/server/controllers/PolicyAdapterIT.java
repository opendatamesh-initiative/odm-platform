package org.opendatamesh.platform.pp.policy.server.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationEventResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.NotificationServiceV2Client;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.emitted.DataProductInitializationApprovedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.emitted.DataProductInitializationRejectedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.emitted.DataProductVersionPublicationApprovedEventRes;
import org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.emitted.DataProductVersionPublicationRejectedEventRes;
import org.opendatamesh.platform.pp.policy.server.services.proxies.ValidatorProxy;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the Policy Service Adapter for Registry 2.0 and Notification 2.0.
 * <p>
 * IMPORTANT:
 * Registry 2.0 does NOT send messages directly to the Policy Service.
 * Instead, Registry 2.0 emits domain events to Notification 2.0.
 * Notification 2.0 then dispatches these events as notifications to
 * all subscribed observers, including the Policy Service Adapter.
 * <p>
 * These tests verify the adapter component responsible for:
 * - Receiving Registry 2.0 domain events via Notification 2.0
 * - Transforming them into Policy Service validation requests
 * - Processing policy validation responses
 * - Emitting transformed results back as Notification 2.0 events
 * <p>
 * The adapter serves as a translation/compatibility layer between:
 * - Registry 2.0 event flow (event emission via Notification 2.0)
 * - Policy Service validation workflow
 * - Registry 1.0 legacy validation message flow (for backward compatibility)
 */

public class PolicyAdapterIT extends PolicyApplicationIT {

    @Autowired
    private NotificationServiceV2Client notificationServiceV2Client;

    @Autowired
    private ValidatorProxy validatorProxy;

    @BeforeEach
    @AfterEach
    public void resetMocks() {
        reset(notificationServiceV2Client);
        reset(validatorProxy);
    }

    private final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    // ======================================================================================
    // Registry 2.0 Adapter Scenarios
    // ======================================================================================

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_INITIALIZATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "true"
     * And: All policies in the response have passed validation
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_INITIALIZATION_APPROVED" event to Registry 2.0
     * And: The event should contain the original dataProductId
     * And: The event should include all policy evaluation results
     * And: The event should be compatible with Registry 2.0 event flow
     */
    @Test
    public void whenDataProductInitRequestedAndValidationPassesThenEmitApprovedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(true);
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":true}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductInitNotification(dataProductId);

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        DataProductInitializationApprovedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductInitializationApprovedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo("DATA_PRODUCT_INITIALIZATION_APPROVED");
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct().getUuid()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventContent().getDataProduct().getFqn()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_INITIALIZATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "false"
     * And: The response contains at least one blocking policy that failed
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_INITIALIZATION_REJECTED" event to Registry 2.0
     * And: The event should contain the original dataProductId
     * And: The event should include details of failed blocking policies
     * And: The event should include details of failed non-blocking policies
     * And: The event should be compatible with Registry 2.0 error handling
     */
    @Test
    public void whenDataProductInitRequestedAndValidationFailsThenEmitRejectedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(false);
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":false,\"reason\":\"Validation failed\"}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductInitNotification(dataProductId);

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        DataProductInitializationRejectedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductInitializationRejectedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo(NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REJECTED.getValue());
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct().getUuid()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventContent().getDataProduct().getFqn()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_INITIALIZATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "false"
     * And: The response contains some policies that failed but none of them are blocking
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_INITIALIZATION_APPROVED" event to Registry 2.0
     * And: The event should contain the original dataProductId
     * And: The event should include details of failed blocking policies
     * And: The event should include details of failed non-blocking policies
     * And: The event should be compatible with Registry 2.0 error handling
     */
    @Test
    public void whenDataProductInitRequestedAndValidationFailsButNoBlockingPoliciesThenEmitApprovedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(false); // Non-blocking policy
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(false); // Policy validation failed
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":false,\"reason\":\"Validation failed\"}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductInitNotification(dataProductId);

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        // Even though validation failed, since no blocking policies failed, it should emit APPROVED event
        DataProductInitializationApprovedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductInitializationApprovedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo("DATA_PRODUCT_INITIALIZATION_APPROVED");
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProduct().getUuid()).isEqualTo(dataProductId);
        assertThat(emittedEvent.getEventContent().getDataProduct().getFqn()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "true"
     * And: All policies in the response have passed validation
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_VERSION_PUBLICATION_APPROVED" event to Registry 2.0
     * And: The event should contain the original dataProductId and dataProductVersion
     * And: The event should include all policy evaluation results
     * And: The event should be compatible with Registry 2.0 event flow
     */
    @Test
    public void whenDataProductVersionPublishRequestedAndValidationPassesThenEmitApprovedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String dataProductVersion = "1.0.0";
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-version-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Version Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(true);
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":true}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductVersionPublishNotification(dataProductId, dataProductVersion);

        // Extract the dataProductVersion UUID from the notification for verification
        String dataProductVersionUuid = notification.getEvent().getEventContent()
                .get("dataProductVersion").get("uuid").asText();

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        DataProductVersionPublicationApprovedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductVersionPublicationApprovedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo(NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_APPROVED.getValue());
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT_VERSION");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getUuid()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getTag()).isEqualTo(dataProductVersion);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct().getUuid()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "false"
     * And: The response contains at least one blocking policy that failed
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_VERSION_PUBLICATION_REJECTED" event to Registry 2.0
     * And: The event should contain the original dataProductId and dataProductVersion
     * And: The event should include details of failed blocking policies
     * And: The event should include details of failed non-blocking policies
     * And: The event should be compatible with Registry 2.0 error handling
     */
    @Test
    public void whenDataProductVersionPublishRequestedAndValidationFailsThenEmitRejectedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String dataProductVersion = "1.0.0";
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-version-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Version Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(false);
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":false,\"reason\":\"Validation failed\"}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductVersionPublishNotification(dataProductId, dataProductVersion);

        // Extract the dataProductVersion UUID from the notification for verification
        String dataProductVersionUuid = notification.getEvent().getEventContent()
                .get("dataProductVersion").get("uuid").asText();

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        DataProductVersionPublicationRejectedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductVersionPublicationRejectedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo(NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_REJECTED.getValue());
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT_VERSION");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getUuid()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getTag()).isEqualTo(dataProductVersion);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct().getUuid()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED"
     * And: The adapter transformed and sent the request to the policy service
     * And: The policy service returns a ValidationResponseResource with result "false"
     * And: The response contains some policies that failed but none of them are blocking
     * When: The adapter receives the validation response
     * Then: The adapter should transform the response to Registry 2.0 format
     * And: The adapter should send a "DATA_PRODUCT_VERSION_PUBLICATION_APPROVED" event to Registry 2.0
     * And: The event should contain the original dataProductId and dataProductVersion
     * And: The event should include details of failed blocking policies
     * And: The event should include details of failed non-blocking policies
     * And: The event should be compatible with Registry 2.0 error handling
     */
    @Test
    public void whenDataProductVersionPublishRequestedAndValidationFailsButNoBlockingPoliciesThenEmitApprovedEvent() throws Exception {
        // Given
        String dataProductId = "urn:org.opendatamesh:dataproducts:test-" + UUID.randomUUID().toString().substring(0, 8);
        String dataProductVersion = "1.0.0";
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-version-checker-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policy-engines"),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Version Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(false); // Non-blocking policy
        policy.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrlFromString("/api/v1/pp/policy/policies"),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        EvaluationResource evaluationResponse = new EvaluationResource();
        evaluationResponse.setEvaluationResult(false); // Policy validation failed
        evaluationResponse.setOutputObject(objectMapper.readTree("{\"allow\":false,\"reason\":\"Validation failed\"}"));

        when(validatorProxy.validatePolicy(any(PolicyResource.class), any(JsonNode.class)))
                .thenReturn(evaluationResponse);

        NotificationV2Res notification = createDataProductVersionPublishNotification(dataProductId, dataProductVersion);

        // Extract the dataProductVersion UUID from the notification for verification
        String dataProductVersionUuid = notification.getEvent().getEventContent()
                .get("dataProductVersion").get("uuid").asText();

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrlFromString("/api/v2/up/observer/notifications"),
                new HttpEntity<>(notification),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(notificationServiceV2Client).notifyEvent(eventCaptor.capture());

        ObjectNode emittedEventNode = (ObjectNode) eventCaptor.getValue();
        assertThat(emittedEventNode).isNotNull();

        // Even though validation failed, since no blocking policies failed, it should emit APPROVED event
        DataProductVersionPublicationApprovedEventRes emittedEvent = objectMapper.convertValue(emittedEventNode, DataProductVersionPublicationApprovedEventRes.class);
        assertThat(emittedEvent).isNotNull();
        assertThat(emittedEvent.getType()).isEqualTo(NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_APPROVED.getValue());
        assertThat(emittedEvent.getResourceType()).isEqualTo("DATA_PRODUCT_VERSION");
        assertThat(emittedEvent.getResourceIdentifier()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventTypeVersion()).isEqualTo("v2.0.0");
        assertThat(emittedEvent.getEventContent()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getUuid()).isEqualTo(dataProductVersionUuid);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getTag()).isEqualTo(dataProductVersion);
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct()).isNotNull();
        assertThat(emittedEvent.getEventContent().getDataProductVersion().getDataProduct().getUuid()).isEqualTo(dataProductId);

        verify(notificationServiceV2Client).processingSuccess(notification.getSequenceId());

        // Cleanup
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policies") + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrlFromString("/api/v1/pp/policy/policy-engines") + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    // ======================================================================================
    // Backward Compatibility Scenarios
    // ======================================================================================

    /**
     * Given: Registry 2.0 sent a validation request with event type "DATA_PRODUCT_INITIALIZATION_REQUESTED"
     * When: The adapter handles the notification
     * Then: The adapter should transform the notification to Registry 1.0 format
     * And: The adapter should send a validation request to the policy service
     * And: Old policies should work as expected
     */


    private NotificationV2Res createDataProductInitNotification(String dataProductId) throws Exception {
        // Create RegistryV2 DataProduct resource
        RegistryV2DataProductRes dataProduct = new RegistryV2DataProductRes();
        dataProduct.setUuid(dataProductId);
        dataProduct.setFqn(dataProductId);
        dataProduct.setDomain("testDomain");
        dataProduct.setName(dataProductId.substring(dataProductId.lastIndexOf(":") + 1));
        dataProduct.setDisplayName("Test Product");
        dataProduct.setDescription("Test Description");
        dataProduct.setCreatedAt(new Date());
        dataProduct.setUpdatedAt(new Date());

        NotificationV2Res notification = new NotificationV2Res();
        notification.setSequenceId(1L);

        NotificationV2Res.NotificationV2EventRes event = new NotificationV2Res.NotificationV2EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier(dataProductId);
        event.setType(NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REQUESTED.getValue());
        event.setEventTypeVersion("v2.0.0");

        // Convert RegistryV2 resource to ObjectNode for event content
        ObjectNode eventContent = objectMapper.createObjectNode();
        ObjectNode dataProductNode = objectMapper.valueToTree(dataProduct);
        eventContent.set("dataProduct", dataProductNode);
        event.setEventContent(eventContent);

        notification.setEvent(event);

        return notification;
    }

    private NotificationV2Res createDataProductVersionPublishNotification(String dataProductId, String dataProductVersion) throws Exception {
        // Load the complete DPV JSON from resources
        ObjectNode dpvContent;
        try (InputStream resourceStream = getClass().getResourceAsStream("dataproduct-version-complete.json")) {
            if (resourceStream == null) {
                throw new IllegalStateException("Could not find dataproduct-version-complete.json in test resources");
            }
            dpvContent = (ObjectNode) objectMapper.readTree(resourceStream);
        }

        // Update dynamic values in the loaded JSON
        String dataProductVersionUuid = "urn:org.opendatamesh:dataproductversions:test-" + UUID.randomUUID().toString().substring(0, 8);

        // Update info section with dynamic values
        if (dpvContent.has("info")) {
            ObjectNode info = (ObjectNode) dpvContent.get("info");
            info.put("fullyQualifiedName", dataProductId);
            info.put("name", dataProductId.substring(dataProductId.lastIndexOf(":") + 1));
            info.put("version", dataProductVersion);
        }

        // Create RegistryV2 DataProduct resource
        RegistryV2DataProductRes dataProduct = new RegistryV2DataProductRes();
        dataProduct.setUuid(dataProductId);
        dataProduct.setFqn(dataProductId);
        dataProduct.setDomain("testDomain");
        dataProduct.setName(dataProductId.substring(dataProductId.lastIndexOf(":") + 1));
        dataProduct.setDisplayName("Test Product");
        dataProduct.setDescription("A comprehensive test data product for policy validation");

        // Create RegistryV2 DataProductVersion resource
        RegistryV2DataProductVersionRes dataProductVersionRes = new RegistryV2DataProductVersionRes();
        dataProductVersionRes.setUuid(dataProductVersionUuid);
        dataProductVersionRes.setTag(dataProductVersion);
        dataProductVersionRes.setName(dataProductId.substring(dataProductId.lastIndexOf(":") + 1) + "-" + dataProductVersion);
        dataProductVersionRes.setDescription("A comprehensive test data product for policy validation");
        dataProductVersionRes.setSpec("opendatamesh");
        dataProductVersionRes.setSpecVersion("1.0.0");
        dataProductVersionRes.setDataProduct(dataProduct);
        dataProductVersionRes.setContent(dpvContent);

        NotificationV2Res notification = new NotificationV2Res();
        notification.setSequenceId(1L);

        NotificationV2Res.NotificationV2EventRes event = new NotificationV2Res.NotificationV2EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier(dataProductId);
        event.setType(NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED.getValue());
        event.setEventTypeVersion("v2.0.0");

        // Convert RegistryV2 resources to ObjectNode for event content
        ObjectNode eventContent = objectMapper.createObjectNode();
        ObjectNode dataProductVersionNode = objectMapper.valueToTree(dataProductVersionRes);
        eventContent.set("dataProductVersion", dataProductVersionNode);

        event.setEventContent(eventContent);

        notification.setEvent(event);

        return notification;
    }

    // ======================================================================================
    // RegistryV2 Resource Classes (Inner Test Classes)
    // ======================================================================================

    /**
     * RegistryV2 DataProduct resource representation for test purposes.
     * This matches the structure expected by the notification system.
     */
    public static class RegistryV2DataProductRes {
        private String uuid;
        private String fqn;
        private String domain;
        private String name;
        private String displayName;
        private String description;
        private String validationState;
        private Date createdAt;
        private Date updatedAt;

        public RegistryV2DataProductRes() {
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getFqn() {
            return fqn;
        }

        public void setFqn(String fqn) {
            this.fqn = fqn;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getValidationState() {
            return validationState;
        }

        public void setValidationState(String validationState) {
            this.validationState = validationState;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    /**
     * RegistryV2 DataProductVersion resource representation for test purposes.
     * This matches the structure expected by the notification system.
     */
    public static class RegistryV2DataProductVersionRes {
        private String uuid;
        private RegistryV2DataProductRes dataProduct;
        private String name;
        private String description;
        private String tag;
        private String validationState;
        private String spec;
        private String specVersion;
        private JsonNode content;
        private String createdBy;
        private String updatedBy;

        public RegistryV2DataProductVersionRes() {
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public RegistryV2DataProductRes getDataProduct() {
            return dataProduct;
        }

        public void setDataProduct(RegistryV2DataProductRes dataProduct) {
            this.dataProduct = dataProduct;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getValidationState() {
            return validationState;
        }

        public void setValidationState(String validationState) {
            this.validationState = validationState;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getSpecVersion() {
            return specVersion;
        }

        public void setSpecVersion(String specVersion) {
            this.specVersion = specVersion;
        }

        public JsonNode getContent() {
            return content;
        }

        public void setContent(com.fasterxml.jackson.databind.JsonNode content) {
            this.content = content;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }
}
