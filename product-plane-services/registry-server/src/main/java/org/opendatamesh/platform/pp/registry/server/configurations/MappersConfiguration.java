package org.opendatamesh.platform.pp.registry.server.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

        /*
         * objectMapper.setSerializationInclusion(Include.NON_EMPTY);
         * 
         * SimpleModule module = new SimpleModule();
         * module.addDeserializer(LifecycleActivityInfoDPDS.class, new
         * ActivityInfoDeserializer());
         * module.addDeserializer(LifecycleInfoDPDS.class, new
         * LifecycleInfoDeserializer());
         * 
         * objectMapper.registerModule(module);
         */
        return objectMapper;
    }

   
    @Bean
    public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(ObjectMapperFactory.JSON_MAPPER);
        return converter;
    }
}
