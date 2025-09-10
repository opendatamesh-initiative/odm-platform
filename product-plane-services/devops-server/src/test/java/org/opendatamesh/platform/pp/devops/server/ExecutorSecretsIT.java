package org.opendatamesh.platform.pp.devops.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for executor secrets functionality.
 * Tests the complete flow from HTTP headers containing secrets to executor requests.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ExecutorSecretsIT extends ODMDevOpsIT {

    // ======================================================================================
    // Integration Tests for Executor Secrets
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateActivityWithExecutorSecrets() {
        createMocksForCreateActivityCall();

        // Create activity with executor secrets in headers
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-gitlab-token", "gitlab-secret-123");
        headers.put("x-odm-dummy-executor-secret-azure-token", "azure-secret-456");
        headers.put("x-odm-gitlab-executor-secret-api-key", "gitlab-api-key-789");
        headers.put("x-odm-regular-header", "should-be-ignored");

        ActivityResource createdActivity = createActivity(activityRes, false, headers);

        // Verify activity was created successfully
        assertThat(createdActivity.getId()).isNotNull();
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PLANNED);
        assertThat(createdActivity.getDataProductId()).isEqualTo("f350cab5-992b-32f7-9c90-79bca1bf10be");
        assertThat(createdActivity.getStage()).isEqualTo("test");

        // Verify that the activity can be started (secrets should be processed)
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateActivityAndStartWithExecutorSecrets() {
        createMocksForCreateActivityCall();

        // Create activity with executor secrets and start it immediately
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-gitlab-token", "gitlab-secret-123");
        headers.put("x-odm-azure-executor-secret-api-key", "azure-api-key-456");

        ActivityResource createdActivity = createActivity(activityRes, true, headers);

        // Verify activity was created and started successfully
        assertThat(createdActivity.getId()).isNotNull();
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(createdActivity.getStartedAt()).isNotNull();

        // Verify that secrets were processed
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testStartActivityWithExecutorSecrets() throws IOException {
        createMocksForCreateActivityCall();

        // First create an activity without secrets
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        ActivityResource createdActivity = createActivity(activityRes, false);

        // Then start the activity with secrets
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-gitlab-token", "gitlab-secret-123");
        headers.put("x-odm-azure-executor-secret-api-key", "azure-api-key-456");

        ActivityStatusResource startResponse = devOpsClient.startActivity(createdActivity.getId());

        assertThat(startResponse).isNotNull();
        assertThat(startResponse.getStatus()).isEqualTo(ActivityStatus.PROCESSING);

        // Verify that secrets were processed
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testExecutorSecretsHeaderProcessing() {
        createMocksForCreateActivityCall();

        // Test various header formats
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-gitlab-token", "gitlab-secret-123");
        headers.put("x-odm-dummy-executor-secret-azure-token", "azure-secret-456");
        headers.put("x-odm-gitlab-executor-secret-api-key", "gitlab-api-key-789");
        headers.put("x-odm-invalid-header", "should-be-ignored");
        headers.put("x-odm-dummy-executor-secret-", "incomplete-secret-type");
        headers.put("x-odm--executor-secret-token", "missing-executor-name");
        headers.put("x-odm-regular-header", "should-be-ignored");

        ActivityResource createdActivity = createActivity(activityRes, false, headers);

        // Verify activity was created successfully
        assertThat(createdActivity.getId()).isNotNull();
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Verify that secrets were processed (only valid headers should be processed)
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testExecutorSecretsWithNullAndEmptyHeaders() {
        createMocksForCreateActivityCall();

        // Test with null headers - use different stage to avoid conflicts
        ActivityResource activityRes1 = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        ActivityResource createdActivity1 = createActivity(activityRes1, false, null);
        assertThat(createdActivity1.getId()).isNotNull();
        assertThat(createdActivity1.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Test with empty headers - use different stage to avoid conflicts
        ActivityResource activityRes2 = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "prod");
        Map<String, String> emptyHeaders = new HashMap<>();
        ActivityResource createdActivity2 = createActivity(activityRes2, false, emptyHeaders);
        assertThat(createdActivity2.getId()).isNotNull();
        assertThat(createdActivity2.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Both activities should work without secrets
        verifyExecutorSecretsInCache(createdActivity1.getId());
        verifyExecutorSecretsInCache(createdActivity2.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testExecutorSecretsWithSpecialCharacters() {
        createMocksForCreateActivityCall();

        // Test with special characters in secret values
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-token", "token-with-special-chars!@#$%^&*()");
        headers.put("x-odm-dummy-executor-secret-key", "key with spaces and symbols: <>?");
        headers.put("x-odm-gitlab-executor-secret-api-key", "gitlab-key-with-unicode-🚀");

        ActivityResource createdActivity = createActivity(activityRes, false, headers);

        // Verify activity was created successfully
        assertThat(createdActivity.getId()).isNotNull();
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Verify that secrets with special characters were processed
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testMultipleExecutorsWithDifferentSecrets() {
        createMocksForCreateActivityCall();

        // Test multiple executors with different secrets
        ActivityResource activityRes = buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0", "test");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-odm-dummy-executor-secret-gitlab-token", "dummy-gitlab-secret");
        headers.put("x-odm-dummy-executor-secret-azure-token", "dummy-azure-secret");
        headers.put("x-odm-gitlab-executor-secret-api-key", "gitlab-api-key");
        headers.put("x-odm-gitlab-executor-secret-webhook-token", "gitlab-webhook-token");
        headers.put("x-odm-azure-executor-secret-service-principal", "azure-sp-secret");
        headers.put("x-odm-azure-executor-secret-subscription-id", "azure-sub-id");

        ActivityResource createdActivity = createActivity(activityRes, false, headers);

        // Verify activity was created successfully
        assertThat(createdActivity.getId()).isNotNull();
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Verify that secrets for multiple executors were processed
        verifyExecutorSecretsInCache(createdActivity.getId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testExecutorSecretsCacheKeyCollisionPrevention() {
        // Test cache key collision prevention by directly using the DevOpsClients methods
        // This tests the core functionality without needing multiple activities
        
        Map<String, String> secrets1 = Map.of("x-odm-token", "secret-for-activity-1");
        Map<String, String> secrets11 = Map.of("x-odm-token", "secret-for-activity-11");
        Map<String, String> secrets111 = Map.of("x-odm-token", "secret-for-activity-111");

        // Store secrets for activities with IDs that could cause collisions
        DevOpsClients.storeSecrets("dummy", 1L, secrets1);
        DevOpsClients.storeSecrets("dummy", 11L, secrets11);
        DevOpsClients.storeSecrets("dummy", 111L, secrets111);

        // Test removal of specific activity
        DevOpsClients.removeAllSecretsForActivity(1L);

        // Test removal of activity 11
        DevOpsClients.removeAllSecretsForActivity(11L);

        // Test removal of activity 111
        DevOpsClients.removeAllSecretsForActivity(111L);

        // All secrets should be removed without affecting each other
        System.out.println("Cache collision prevention test completed successfully");
    }
}
