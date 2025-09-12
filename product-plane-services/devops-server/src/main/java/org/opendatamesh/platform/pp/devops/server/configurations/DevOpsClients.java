package org.opendatamesh.platform.pp.devops.server.configurations;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import org.opendatamesh.platform.pp.devops.server.clients.ExecutorClientWithSecrets;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class DevOpsClients {

    private static final Logger logger = LoggerFactory.getLogger(DevOpsClients.class);

    /**
     * Cache for storing executor secrets for activities.
     * Secrets expire after 1 hour to prevent memory leaks.
     */
    private static final Cache<String, Map<String, String>> secretsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    DevOpsConfigurations configs;
    RegistryClient registryClient;
    Map<String, ExecutorClientWithSecrets> executorsClients; 

    @Autowired
    public DevOpsClients(DevOpsConfigurations configs) {
        this.configs = configs;
        registryClient = new RegistryClient(configs.getProductPlane().getRegistryService().getAddress());
        executorsClients = new HashMap<String, ExecutorClientWithSecrets>(); 
        for(String adapterName : configs.getUtilityPlane().getExecutorServices().keySet()) {
            DevOpsConfigurations.ExecutorServicesConfigs executorServicesConfigs =
                    configs.getUtilityPlane().getExecutorServices().get(adapterName);
            if(executorServicesConfigs != null && executorServicesConfigs.getActive()) {
                executorsClients.put(adapterName, new ExecutorClientWithSecrets(
                        executorServicesConfigs.getAddress(),
                        executorServicesConfigs.getCheckAfterCallback()
                ));
            }
        }
    }

    /**
     * Gets an ExecutorClientWithSecrets for the specified adapter and activity.
     * If secrets are available in the cache, they will be included in the client.
     * 
     * @param adapterName The name of the executor adapter
     * @param activityId The ID of the activity
     * @return ExecutorClientWithSecrets with secrets if available, or without secrets
     */
    public ExecutorClientWithSecrets getExecutorClient(String adapterName, Long activityId) {
        ExecutorClientWithSecrets baseClient = executorsClients.get(adapterName);
        if (baseClient == null) {
            return null;
        }

        // Check if we have secrets for this executor and activity
        String cacheKey = generateCacheKey(adapterName, activityId);
        Map<String, String> secretHeaders = secretsCache.getIfPresent(cacheKey);
        
        if (secretHeaders != null && !secretHeaders.isEmpty()) {
            // Create a new instance with the secrets
            return new ExecutorClientWithSecrets(
                baseClient.getServerAddress(),
                baseClient.getCheckAfterCallback(),
                secretHeaders
            );
        }
        
        // Return the base client without secrets
        return baseClient;
    }

    /**
     * Gets the base ExecutorClientWithSecrets without secrets.
     *
     * @param adapterName The name of the executor adapter
     * @return ExecutorClientWithSecrets without secrets
     */
    public ExecutorClientWithSecrets getExecutorClient(String adapterName) {
        return executorsClients.get(adapterName);
    }

    /**
     * Stores secret headers for a specific executor and activity.
     * 
     * @param executorName The name of the executor
     * @param activityId The ID of the activity
     * @param secretHeaders Map of transformed headers ready for use
     */
    public static void storeSecrets(String executorName, Long activityId, Map<String, String> secretHeaders) {
        if (executorName == null || activityId == null || secretHeaders == null) {
            return;
        }
        
        String cacheKey = generateCacheKey(executorName, activityId);
        secretsCache.put(cacheKey, secretHeaders);
    }

    /**
     * Removes all secrets for a specific activity.
     * This is useful when an activity is completed and all its secrets should be cleaned up.
     * 
     * @param activityId The ID of the activity
     */
    public static void removeAllSecretsForActivity(Long activityId) {
        if (activityId == null) {
            return;
        }
        
        // Get all cache keys and remove those that match the activity ID
        // Cache key format: "executorName-ID-activityId"
        String activitySuffix = "-ID-" + activityId;
        secretsCache.asMap().keySet().removeIf(key -> key.endsWith(activitySuffix));
    }

    /**
     * Extracts executor secrets from HTTP headers and stores them in the cache.
     * This method extracts headers matching the pattern "x-odm-<executorName>-executor-secret-<secretType>"
     * and transforms them to "x-odm-<secretType>" for storage in the cache.
     * 
     * @param headers The HTTP headers containing executor secrets
     * @param activityId The activity ID to associate the secrets with
     */
    public static void extractAndStoreExecutorSecrets(Map<String, String> headers, Long activityId) {
        if (headers == null || activityId == null) {
            return;
        }

        // Group secrets by executor name
        Map<String, Map<String, String>> executorSecrets = new HashMap<>();

        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerName = header.getKey();
            String headerValue = header.getValue();

            // Filter headers that match the pattern: x-odm-<executorName>-executor-secret-<secretType>
            if (headerName.startsWith("x-odm-") && headerName.contains("-executor-secret-")) {
                // Extract executor name: between "x-odm-" and "-executor-secret-"
                String executorSecretMarker = "-executor-secret-";
                int executorSecretIndex = headerName.indexOf(executorSecretMarker);
                
                if (executorSecretIndex > 6) { // 6 is length of "x-odm-"
                    String executorName = headerName.substring(6, executorSecretIndex); // 6 is length of "x-odm-"
                    
                    // Extract secret type: everything after "-executor-secret-"
                    String secretType = headerName.substring(executorSecretIndex + executorSecretMarker.length());
                    
                    // Create transformed header name: x-odm-<secretType>
                    String transformedHeaderName = "x-odm-" + secretType;

                    // Store in executor secrets map
                    executorSecrets.computeIfAbsent(executorName, k -> new HashMap<>())
                            .put(transformedHeaderName, headerValue);
                }
            }
        }

        // Store secrets in cache for each executor
        for (Map.Entry<String, Map<String, String>> executorEntry : executorSecrets.entrySet()) {
            String executorName = executorEntry.getKey();
            Map<String, String> secrets = executorEntry.getValue();
            
            storeSecrets(executorName, activityId, secrets);
            logger.debug("Stored {} secrets for executor '{}' and activity {}", 
                        secrets.size(), executorName, activityId);
        }
    }

    /**
     * Generates a cache key from executor name and activity ID.
     * 
     * @param executorName The name of the executor
     * @param activityId The ID of the activity
     * @return The cache key in format "executorName-ID-activityId"
     */
    private static String generateCacheKey(String executorName, Long activityId) {
        return executorName + "-ID-" + activityId;
    }

}
