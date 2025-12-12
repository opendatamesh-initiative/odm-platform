package org.opendatamesh.platform.pp.policy.server.controllers;

import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;

/**
 * Test configuration for integration tests.
 * This class is included in the SpringBootTest configuration to provide
 * test-specific beans and configurations.
 */
@TestConfiguration
public class TestConfig {

    // Test configuration can be added here if needed

    //Makes all @Async methods synchronous
    @Bean
    @Primary
    public Executor taskExecutor() {
        return new SyncTaskExecutor();
    }

    /**
     * Scans for all interfaces under the client packages and proxy service classes
     * under the services/proxies packages, and registers Mockito.mock(clazz) as singleton beans.
     */
    @Bean
    public static BeanFactoryPostProcessor mockClientsAndProxiesRegistrar() {
        return beanFactory -> {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resolver);
            
            // Patterns to scan for mocks
            String[] patterns = {
                "classpath*:org/opendatamesh/platform/pp/policy/server/client/**/*.class",
                "classpath*:org/opendatamesh/platform/pp/policy/server/adapter/client/**/*.class",
                "classpath*:org/opendatamesh/platform/pp/policy/server/services/proxies/**/*.class"
            };
            
            try {
                for (String pattern : patterns) {
                    Resource[] resources = resolver.getResources(pattern);
                    for (Resource resource : resources) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        Class<?> clazz = ClassUtils.forName(className, TestConfig.class.getClassLoader());
                        
                        boolean shouldMock = false;
                        if (pattern.contains("/client/")) {
                            // Mock interfaces in client packages (including adapter/client)
                            shouldMock = clazz.isInterface();
                        } else if (pattern.contains("/proxies/")) {
                            // Mock service classes in proxies packages
                            shouldMock = clazz.isAnnotationPresent(Service.class) && 
                                        !clazz.isInterface() && 
                                        !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers());
                        }
                        
                        if (shouldMock) {
                            String beanName = StringUtils.uncapitalize(clazz.getSimpleName());
                            // Remove all beans of this type
                            String[] beanNamesForType = factory.getBeanNamesForType(clazz);
                            for (String existingBeanName : beanNamesForType) {
                                factory.removeBeanDefinition(existingBeanName);
                            }
                            Object mock = Mockito.mock(clazz);
                            factory.registerSingleton(beanName, mock);
                        }
                    }
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to register mock clients and proxy services", ex);
            }
        };
    }
}
